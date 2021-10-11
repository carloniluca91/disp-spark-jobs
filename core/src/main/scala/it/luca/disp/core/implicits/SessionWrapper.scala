package it.luca.disp.core.implicits

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.SparkSession

import java.sql.Timestamp
import java.time.Instant
import java.time.format.DateTimeFormatter

class SessionWrapper(protected val sparkSession: SparkSession) {
  
  /**
   * Get application id
   * @return
   */

  def applicationId: String = sparkSession.sparkContext.applicationId

  /**
   * Get application name
   * @return
   */

  def appName: String = sparkSession.sparkContext.appName
  
  /**
   * Get underlying instance of [[FileSystem]]
   * @return instance of [[FileSystem]]
   */
    
  def getFileSystem: FileSystem = FileSystem.get(sparkSession.sparkContext.hadoopConfiguration)

  /**
   * Get start time of current Spark application as [[Timestamp]]
   * @return [[Timestamp]] representing start time of current Spark application
   */

  def startTimeAsTimestamp : Timestamp = Timestamp
    .from(Instant.ofEpochMilli(sparkSession.sparkContext.startTime))

  /**
   * Get start time of current Spark application as a string with given pattern
   * @param pattern pattern for output string
   * @return string representing start time of current Spark application
   */

  def startTimeAsString(pattern: String): String = startTimeAsTimestamp
    .toLocalDateTime.format(DateTimeFormatter.ofPattern(pattern))
}
