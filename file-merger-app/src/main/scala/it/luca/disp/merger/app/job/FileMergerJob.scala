package it.luca.disp.merger.app.job

import it.luca.disp.core.Logging
import it.luca.disp.core.job.SparkJob
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
  protected final val minimumFileSize: Long = properties.getLong("spark.merger.fileSize.minimum.bytes")
  protected final val fileNumberThreshold: Int = properties.getInt("spark.merger.smallFile.maximum")
  protected final val sparkOutputTmpPath: String = properties.getString("spark.merger.output.tmp.path")
  protected final val yarnUiUrl: String = properties.getString("yarn.logs.ui.url")

  /**
   * Get an instance of [[MergerLogRecord]] from a merging operation on a partitioned table
   * @param table processed [[Table]]
   * @param partitionColumn table's partition [[Column]]
   * @param partitionValue table's partition value
   * @param optionalThrowable optional exception raised by merging operation
   * @return instance of [[MergerLogRecord]]
   */

  protected final def recordForPartitionedTable(table: Table,
                                                partitionColumn: Column,
                                                partitionValue: String,
                                                optionalThrowable: Option[Throwable]): MergerLogRecord =
    MergerLogRecord(sparkSession, table, partitionColumn, partitionValue, optionalThrowable, yarnUiUrl)

  /**
   * Get an instance of [[MergerLogRecord]] from a merging operation on a non-partitioned table
   * @param table processed [[Table]]
   * @param optionalThrowable optional exception raised by merging operation
   * @return instance of [[MergerLogRecord]]
   */

  protected final def recordForNonPartitionedTable(table: Table,
                                                   optionalThrowable: Option[Throwable]): MergerLogRecord = {
    MergerLogRecord(sparkSession, table, optionalThrowable, yarnUiUrl)
  }

  /**
   * Execute job
   */

  def run(): Unit = {

    val currentDb: String = sparkSession.catalog.currentDatabase
    val tables: Seq[Table] = sparkSession.catalog.listTables
      .filter(t => !t.tableType.equalsIgnoreCase("view"))
      .collect()

    log.info(s"Found ${tables.size} table(s) within db $currentDb")

    // Link every table to its potential partition column and handle them accordingly
    val tablesMaybeWithPartitionColumn: Seq[(Table, Option[Column])] = tables.map { t => (t, sparkSession.getOptionalPartitionColumn(t.name)) }
    val logRecordsFromPartitionedTables: Seq[MergerLogRecord] = tablesMaybeWithPartitionColumn.flatten {
      case (table, maybeColumn) if maybeColumn.isDefined => handlePartitionedTable(table, maybeColumn.get) }

    val logRecordsFromNonPartitionedTables: Seq[MergerLogRecord] = tablesMaybeWithPartitionColumn
      .collect { case (table, maybeColumn) if maybeColumn.isEmpty => handleNonPartitionedTable(table) }
      .collect { case Some(x) => x }

    // Write generated collection of log records
    log.info(s"Analyzed all of ${tables.size} table(s) within db $currentDb")
    writeLogRecords(logRecordsFromPartitionedTables ++ logRecordsFromNonPartitionedTables)
  }

  /**
   * Execute job on a partitioned table
   * @param table [[Table]] to process
   * @param partitionColumn table's partition [[Column]]
   * @return collection of [[MergerLogRecord]] (one for each partition which has been merged)
   */

  protected def handlePartitionedTable(table: Table, partitionColumn: Column): Seq[MergerLogRecord] = {

    val (tableName, tableLocation): (String, String) = (table.name, sparkSession.getTableLocation(table.name))
    log.info(s"Location of table $tableName is $tableLocation. Looking for table partitions to be eventually merged")
    val logRecords: Seq[MergerLogRecord] = fs.getPartitionDirectories(tableLocation)
      .filter { p => fs.containsTooManySmallFiles(p.getPath, fileNumberThreshold, minimumFileSize) }
      .map { p =>

        val partitionName: String = p.getPath.getName
        val partitionValue: String = partitionName.split(s"=")(1)
        val overallSizeOfPartitionFiles: Double = fs.getTotalSizeOfFilesInBytes(p.getPath)
        val optionalThrowable: Option[Throwable] = Try {

          // Compute optimal number of output files, repartition to it and write back using saveMode OverWrite
          val numPartitions: Int = Math.ceil(overallSizeOfPartitionFiles / minimumFileSize.toDouble).toInt
          log.info(s"Found more than $fileNumberThreshold small file(s) within partition $partitionName of table $tableName. Merging them into $numPartitions file(s)")
          val dataFrameWithMergedPartitionFiles: DataFrame = sparkSession.table(tableName)
            .filter(col(partitionColumn.name) === partitionValue)
            .coalesce(numPartitions)

          super.insertInto(dataFrameWithMergedPartitionFiles, tableName, SaveMode.Append)
        } match {
          case Failure(exception) =>
            log.error(s"Caught exception while merging files within partition $partitionName of table $tableName. Stack trace: ", exception)
            Some(exception)
          case Success(_) =>
            log.info(s"Successfully merged files within partition $partitionName of table $tableName")
            None
        }

        recordForPartitionedTable(table, partitionColumn, partitionValue, optionalThrowable)
      }

    log.info(s"Successfully checked partitions of table $tableName")
    logRecords
  }

  /**
   * Execute job on a non-partitioned table
   * @param table [[Table]] to process
   * @return optional [[MergerLogRecord]] (defined if given table has been merged)
   */

  protected def handleNonPartitionedTable(table: Table): Option[MergerLogRecord] = {

    val (tableName, tableLocation): (String, String) = (table.name, sparkSession.getTableLocation(table.name))
    log.info(s"Location of table $tableName is $tableLocation. Starting to check if the table files need to be merged")
    val tableLocationPath: Path = new Path(tableLocation)
    if (fs.containsTooManySmallFiles(tableLocationPath, fileNumberThreshold, minimumFileSize)) {

      // Write content of this table in a temporary path on HDFS, then read it back and write using saveMode OverWrite
      val optionalThrowable: Option[Throwable] = Try {
        val overallSizeOfTableFiles: Double = fs.getTotalSizeOfFilesInBytes(tableLocationPath)
        val numPartitions: Int = Math.ceil(overallSizeOfTableFiles / minimumFileSize.toDouble).toInt
        log.info(s"Found more than $fileNumberThreshold small files within table $tableName. Merging them into $numPartitions file(s)")

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

      Some(recordForNonPartitionedTable(table, optionalThrowable))
    } else {
      log.info(s"No action required for table $tableName")
      None
    }
  }

  /**
   * Save some instances of [[MergerLogRecord]] for logging purposes
   * @param logRecords collection of [[MergerLogRecord]]
   */

  protected def writeLogRecords(logRecords: Seq[MergerLogRecord]): Unit = {

    val recordsDescription = s"${logRecords.size} ${classOf[MergerLogRecord].getSimpleName}(s)"
    Try {

      import sparkSession.implicits._

      val logTableName: String = properties.getString("spark.merger.logTable")
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
