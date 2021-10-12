package it.luca.disp.merger.core

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.{DataFrame, SparkSession}

package object implicits {

  implicit def toDataFrameExtendedWrapper(dataFrame: DataFrame): DataFrameExtendedWrapper =
    new DataFrameExtendedWrapper(dataFrame)

  implicit def toFsWrapper(fs: FileSystem): FsWrapper =
    new FsWrapper(fs)

  implicit def toSessionExtendedWrapper(ss: SparkSession): SessionExtendedWrapper =
    new SessionExtendedWrapper(ss)

}
