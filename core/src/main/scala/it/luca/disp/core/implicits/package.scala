package it.luca.disp.core

import org.apache.spark.sql.{DataFrame, SparkSession}

package object implicits {

  implicit def toDataFrameWrapper(df: DataFrame): DataFrameWrapper =
    new DataFrameWrapper(df)

  implicit def toSessionWrapper(sparkSession: SparkSession): SessionWrapper =
    new SessionWrapper(sparkSession)
}
