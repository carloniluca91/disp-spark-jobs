package it.luca.disp.merger.core.implicits

import it.luca.disp.core.implicits.SessionWrapper
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalog.Column
import org.apache.spark.sql.functions.{col, lower}

class SessionExtendedWrapper(override protected val sparkSession: SparkSession)
  extends SessionWrapper(sparkSession) {

  def getOptionalPartitionColumn(tableName: String): Option[Column] = {

    sparkSession.catalog.listColumns(tableName)
      .filter(_.isPartition).collect()
      .headOption
  }

  /**
   * Get the location of a table
   * @param fqTableName fully qualified (i.e. db.table) table name
   * @return HDFS location of given table
   */

  def getTableLocation(fqTableName: String): String = {

    sparkSession.sql(s"DESCRIBE FORMATTED $fqTableName")
      .filter(lower(col("col_name")) === "location")
      .select(col("data_type"))
      .collect.head
      .getAs[String](0)
  }

}
