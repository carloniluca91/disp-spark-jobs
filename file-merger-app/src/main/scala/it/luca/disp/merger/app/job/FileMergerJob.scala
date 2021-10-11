package it.luca.disp.merger.app.job

import it.luca.disp.core.implicits._
import it.luca.disp.core.Logging
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalog.{Column, Table}

import java.sql.Connection
import scala.util.matching.Regex

class FileMergerJob(protected val sparkSession: SparkSession,
                    protected val impalaConnection: Connection,
                    protected val properties: PropertiesConfiguration)
  extends Logging {

  protected final val fs: FileSystem = sparkSession.getFileSystem
  protected final val dbName: String = properties.getString("spark.default.database")
  protected final val minimumFileSize: Long = properties.getLong("spark.fileSize.minimum.bytes")

  def run(): Unit = {

    val tables: Seq[Table] = sparkSession.catalog.listTables(dbName)
      .filter(t => !t.tableType.equalsIgnoreCase("view"))
      .collect()

    log.info(s"Found ${tables.size} table(s) within db $dbName")
    tables.foreach { t =>

      val partitionColumns: Seq[Column] = sparkSession.catalog.listColumns(t.database, t.name)
        .filter(c => c.isPartition)
        .collect().toSeq

      if (partitionColumns.nonEmpty) {
        handlePartitionedTable(t, partitionColumns.head.name)
      }
    }
  }

  protected def handlePartitionedTable(table: Table, partitionColumn: String): Unit = {

    val fqTableName: String = s"${table.database}.${table.name}"
    val tableLocation: String = sparkSession.getTableLocation(fqTableName)
    log.info(s"Location of table $fqTableName is $tableLocation. Looking for table partitions to be eventually merged")
    val partitionsToCheck: Seq[FileStatus] = fs.listStatus(new Path(tableLocation))
      .filter(p => p.isDirectory)

    partitionsToCheck.foreach { p =>

      val partitionsFiles: Seq[FileStatus] = fs.listStatus(p.getPath).filter(f => f.isFile)
      val smallPartitionFiles: Seq[FileStatus] = fs.listStatus(p.getPath).filter(f => f.getLen < minimumFileSize)
      if (smallPartitionFiles.size > 1) {

        val overallSizeOfPartitionFiles: Double = partitionsFiles.map(_.getLen).sum
        val numPartitions: Int = Math.ceil(overallSizeOfPartitionFiles / minimumFileSize.toDouble).toInt
        val smallPartitionFilesDescription: String = smallPartitionFiles.map(f => s"  ${f.getPath}").mkString("\n").concat("\n")
        log.info(s"Found ${smallPartitionFiles.size} small files within partition ${p.getPath}\n\n$smallPartitionFilesDescription")
      }
    }
  }
}
