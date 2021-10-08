package it.luca.disp.core

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.SparkSession

package object implicits {

  implicit def toFileSystemWrapper(fs: FileSystem): FileSystemWrapper = new FileSystemWrapper(fs)

  implicit def toSparkSessionWrapper(sparkSession: SparkSession): SparkSessionWrapper = new SparkSessionWrapper(sparkSession)

}
