package it.luca.disp.core.job

import it.luca.disp.core.Logging
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{Column, DataFrame, SaveMode, SparkSession}

import java.sql.{Connection, SQLException}

abstract class SparkJob(protected val sparkSession: SparkSession,
                        protected val impalaConnection: Connection)
  extends Logging {

  /**
   * Write given [[DataFrame]] to an Hive table using given [[SaveMode]]
   * @param dataFrame dataFrame
   * @param fqTargetTableName fully qualified (i.e. db.table) name of target table
   * @param saveMode saveMode to use
   * @throws SQLException in case of issues with Impala statement execution
   */

  @throws[SQLException]
  protected def insertInto(dataFrame: DataFrame, fqTargetTableName: String, saveMode: SaveMode): Unit = {

    val targetTableColumns: Seq[Column] = sparkSession.table(fqTargetTableName).columns.map(col)
    val matchedDataFrame: DataFrame = dataFrame.select(targetTableColumns: _*)
    log.info(s"Successfully matched data with target table $fqTargetTableName. " +
      s"Starting to insert data with saveMode $saveMode\n\n  ${matchedDataFrame.schema.treeString}")

    matchedDataFrame.write.mode(saveMode).insertInto(fqTargetTableName)
    log.info(s"Successfully inserted data into $fqTargetTableName with saveMode $saveMode")

    // Issue an ImpalaQl statement
    val impalaStatement = if (saveMode == SaveMode.Overwrite) s"INVALIDATE METADATA $fqTargetTableName" else s"REFRESH $fqTargetTableName"
    log.info(s"Issuing following ImpalaQl statement: $impalaStatement")
    impalaConnection.createStatement.executeUpdate(impalaStatement)
    log.info("Successfully issued ImpalaQl statement $impalaStatement")
  }
}
