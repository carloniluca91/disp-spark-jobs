package it.luca.disp.merger.app.job

import it.luca.disp.core.Logging
import it.luca.disp.core.job.SparkJob
import it.luca.disp.merger.app.dto.MergeOperationInfo
import it.luca.disp.merger.core.implicits._
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.catalog.{Column, Table}
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.sql.Connection
import scala.util.{Failure, Success, Try}

class FileMergerJob(override protected val sparkSession: SparkSession,
                    override protected val impalaConnection: Connection,
                    protected val properties: PropertiesConfiguration)
  extends SparkJob(sparkSession, impalaConnection)
    with Logging {

  protected final val fs: FileSystem = sparkSession.getFileSystem
  protected final val dbName: String = properties.getString("spark.default.database")
  protected final val minimumFileSize: Long = properties.getLong("spark.fileSize.minimum.bytes")
  protected final val maximumNumberOfSmallFiles: Int = properties.getInt("spark.smallFile.maximum")
  protected final val sparkOutputTmpPath: String = properties.getString("spark.output.tmp.path")
  protected final val yarnUiUrl: String = properties.getString("yarn.logs.ui.url")

  def run(): Unit = {

    // Get list of tables whose status should be analyzed
    sparkSession.catalog.setCurrentDatabase(dbName)
    log.info(s"Set current database to $dbName for this ${classOf[SparkSession].getSimpleName}")
    val tables: Seq[Table] = sparkSession.catalog.listTables
      .filter(t => !t.tableType.equalsIgnoreCase("view"))
      .collect()

    log.info(s"Found ${tables.size} table(s) within db $dbName")

    // Link every table to its potential partition column and handle them accordingly
    val tablesMaybeWithPartitionColumn: Seq[(Table, Option[Column])] = tables.map { t => (t, sparkSession.getOptionalPartitionColumn(t.name)) }
    val logRecordsFromPartitionedTables: Seq[FileMergerLogRecord] = tablesMaybeWithPartitionColumn.flatten {
      case (table, maybeColumn) if maybeColumn.isDefined => handlePartitionedTable(table, maybeColumn.get) }

    val logRecordsFromNonPartitionedTables: Seq[FileMergerLogRecord] = tablesMaybeWithPartitionColumn
      .collect { case (table, maybeColumn) if maybeColumn.isEmpty => handleNonPartitionedTable(table) }
      .collect { case Some(x) => x }

    // Write generated collection of log records
    writeLogRecords(logRecordsFromPartitionedTables ++ logRecordsFromNonPartitionedTables)
  }

  protected def handlePartitionedTable(table: Table, partitionColumn: Column): Seq[FileMergerLogRecord] = {

    val (tableName, tableLocation): (String, String) = (table.name, sparkSession.getTableLocation(table.name))
    log.info(s"Location of table $tableName is $tableLocation. Looking for table partitions to be eventually merged")
    val logRecords: Seq[FileMergerLogRecord] = fs.getPartitionDirectories(tableLocation)
      .filter { p => fs.needsToBeMerged(p.getPath, minimumFileSize, maximumNumberOfSmallFiles) }
      .map { p =>

        val partitionValue: String = p.getPath.getName.split(s"=")(1)
        val overallSizeOfPartitionFiles: Double = fs.getTotalSizeOfFilesInBytes(p.getPath)
        val optionalThrowable: Option[Throwable] = Try {

          // Compute optimal number of output files, repartition to it and write back using saveMode OverWrite
          val numPartitions: Int = Math.ceil(overallSizeOfPartitionFiles / minimumFileSize.toDouble).toInt
          log.info(s"Found more than $maximumNumberOfSmallFiles small file(s) within partition ${p.getPath} of table $tableName. Merging them into $numPartitions file(s)")
          val dataFrameWithMergedPartitionFiles: DataFrame = sparkSession.table(tableName)
            .filter(col(partitionColumn.name) === partitionValue)
            .coalesce(numPartitions)

          super.insertInto(dataFrameWithMergedPartitionFiles, tableName, SaveMode.Append)
        } match {
          case Failure(exception) =>
            log.error(s"Caught exception while merging files within partition ${p.getPath} of table $tableName. Stack trace: ", exception)
            Some(exception)
          case Success(_) =>
            log.info(s"Successfully merged files within partition ${p.getPath} of table $tableName")
            None
        }

        FileMergerLogRecord(sparkSession, MergeOperationInfo(tableName, partitionColumn.name, partitionValue), yarnUiUrl, optionalThrowable)
      }

    log.info(s"Successfully checked partitions of table $tableName")
    logRecords
  }

  protected def handleNonPartitionedTable(table: Table): Option[FileMergerLogRecord] = {

    val (tableName, tableLocation): (String, String) = (table.name, sparkSession.getTableLocation(table.name))
    log.info(s"Location of table $tableName is $tableLocation. Starting to check if the table files need to be merged")
    val tableLocationPath: Path = new Path(tableLocation)
    if (fs.needsToBeMerged(tableLocationPath, minimumFileSize, maximumNumberOfSmallFiles)) {

      // Write content of this table in a temporary path on HDFS, then read it back and write using saveMode OverWrite
      val optionalThrowable: Option[Throwable] = Try {
        val overallSizeOfTableFiles: Double = fs.getTotalSizeOfFilesInBytes(tableLocationPath)
        val numPartitions: Int = Math.ceil(overallSizeOfTableFiles / minimumFileSize.toDouble).toInt
        log.info(s"Found more than $maximumNumberOfSmallFiles small files within table $tableName. Merging them into $numPartitions file(s)")

        sparkSession.table(tableName)
          .coalesce(numPartitions)
          .write.mode(SaveMode.Overwrite).parquet(sparkOutputTmpPath)

        log.info(s"Successfully saved data at temporary path $sparkOutputTmpPath")
        val dataFrameWithTableMergedFiles: DataFrame = sparkSession.read.parquet(sparkOutputTmpPath).coalesce(numPartitions)
        super.insertInto(dataFrameWithTableMergedFiles, tableName, SaveMode.Append)
      } match {
        case Failure(exception) => log.error(s"Caught exception while merging files within table $tableName. Stack trace: ", exception); Some(exception)
        case Success(_) => log.info(s"Successfully merged files within table $tableName"); None
      }

      Some(FileMergerLogRecord(sparkSession, MergeOperationInfo(tableName), yarnUiUrl, optionalThrowable))
    } else {
      log.info(s"No operation required for table $tableName")
      None
    }
  }

  protected def writeLogRecords(logRecords: Seq[FileMergerLogRecord]): Unit = {

    val recordsDescription = s"${logRecords.size} ${classOf[FileMergerLogRecord].getSimpleName}(s)"
    Try {

      import sparkSession.implicits._

      val logTableName: String = properties.getString("spark.log.table.fileMerger")
      val logRecordsDf: DataFrame = logRecords.toDF()
        .coalesce(1)
        .withSqlNamingConvention()

      super.insertInto(logRecordsDf, logTableName, SaveMode.Append)
    } match {
      case Failure(exception) => log.error(s"Caught exception while saving $recordsDescription. Stack trace: ", exception)
      case Success(_) => log.info(s"Successfully saved all of $recordsDescription")
    }
  }
}
