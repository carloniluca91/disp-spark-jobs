package it.luca.disp.streaming.core.job

import it.luca.disp.core.Logging
import it.luca.disp.core.implicits._
import it.luca.disp.core.job.SparkJob
import it.luca.disp.streaming.core.ObjectDeserializer.deserializeAsMsgWrapper
import it.luca.disp.streaming.core.StringConsumerRecord
import it.luca.disp.streaming.core.implicits._
import it.luca.disp.streaming.core.dto.{FailedRecordOperation, RecordOperation, SuccessfulConversion}
import it.luca.disp.streaming.model.MsgWrapper
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{lit, udf}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.sql.{Connection, Timestamp}
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZoneId, ZonedDateTime}
import scala.util.{Failure, Success, Try}

/**
 * Base class to extend in order to implement Spark job that converts instances of [[ConsumerRecord]] to [[DataFrame]] and write them in Hive
 * @param sparkSession active [[SparkSession]]
 * @param impalaConnection [[Connection]] to Impala (used for issuing "refresh" or "invalidate metadata" statements
 * @param properties [[PropertiesConfiguration]] holding Spark application properties
 * @param tClass [[Class]] to be used for deserializing [[ConsumerRecord]]'s value which is then converted to a [[DataFrame]]
 * @tparam T type of deserialized data, to be then converted to [[DataFrame]]
 */

abstract class StreamingJob[T](override protected val sparkSession: SparkSession,
                               override protected val impalaConnection: Connection,
                               protected val properties: PropertiesConfiguration,
                               protected val tClass: Class[T])
  extends SparkJob(sparkSession, impalaConnection)
    with Logging {

  // Attributs to be implemented by subclasses
  protected val targetTable: String
  protected val saveMode: SaveMode

  // Common attributes and partitioning function
  protected final val streamingLogTable: String = properties.getString("spark.streaming.logTable")
  protected final val yarnUiUrl: String = properties.getString("yarn.logs.ui.url")
  protected final val gasDayUdf: UserDefinedFunction = udf(StreamingJob.gasDay)

  /**
   * Process a collection of [[ConsumerRecord]]
   * @param records collection of [[ConsumerRecord]]
   * @return an [[Option]] with the offset to be committed by the consumer
   */

  def processBatch(records: Seq[StringConsumerRecord]): Option[Long] = {

    // Convert all records to dataFrames
    val conversionOutputs: Seq[RecordOperation] = records.map(processMessage)
    val logRecordsFromFailedConversions: Seq[IngestionLogRecord] = conversionOutputs.collect { case f: FailedRecordOperation => getLogRecordFrom(f) }
    val successfulConversions: Seq[SuccessfulConversion] = conversionOutputs.collect { case s: SuccessfulConversion => s }

    // If some conversion succeeded, write converted dataFrames to Hive
    val (optionalOffset, logRecordsFromWriteOperation): (Option[Long], Seq[IngestionLogRecord]) = if (successfulConversions.isEmpty) {
      log.error("None of the consumer record(s) from this batch has been successfully converted")
      (None, Seq.empty[IngestionLogRecord])
    } else {

      val dataFrame: DataFrame = successfulConversions.map { _.dataFrame }.reduce { _ union _}
      log.info(s"Successfully reduced all of ${successfulConversions.size} ${classOf[DataFrame].getSimpleName}(s)")
      Try { super.insertInto(dataFrame.coalesce(1), targetTable, saveMode) } match {
        case Failure(exception) =>

          // Create log records reporting exception on writing operation
          val logRecordsFromFailedWrite: Seq[IngestionLogRecord] = successfulConversions.map { x =>
            val failedRecordOperation = FailedRecordOperation(x.record, exception)
            this.getLogRecordFrom(failedRecordOperation)
          }

          (None, logRecordsFromFailedWrite)
        case Success(_) =>

          // Compute offset to commit and create log records reporting succeeded writing operation
          val offsetToCommit: Long = successfulConversions.map { _.record.offset()}.max
          (Some(offsetToCommit), successfulConversions.map { getLogRecordFrom })
      }
    }

    this.writeLogRecords(logRecordsFromFailedConversions ++ logRecordsFromWriteOperation)
    optionalOffset
  }

  /**
   * Process a single [[ConsumerRecord]]
   * @param record [[ConsumerRecord]] to be processed
   * @return either a [[FailedRecordOperation]] if processing fails, or a [[SuccessfulConversion]] otherwise
   */

  protected[job] def processMessage(record: StringConsumerRecord): RecordOperation = {

    val (topic, partition, offset): (String, Int, Long) = (record.topic, record.partition, record.offset)
    val topicPartition: String = s"$topic-$partition"
    log.info(s"Converting record # $offset of topic partition $topicPartition")
    Try {

      // Deserialize record's value as a typed wrapper and convert this to a dataFrame
      val wrapper: MsgWrapper[T] = deserializeAsMsgWrapper(record.value, tClass)
      toDataFrame(wrapper.getPayload)
        .withColumn("record_offset", lit(offset))
        .withColumn("record_topic", lit(topic))
        .withColumn("record_partition", lit(partition))
        .withColumn("record_ts", lit(record.getRecordTimestamp))
        .withColumn("record_dt", lit(record.getRecordDate))
        .withColumn("application_id", lit(sparkSession.applicationId))
        .withColumn("insert_ts", lit(Timestamp.valueOf(LocalDateTime.now())))
        .withColumn("insert_dt", lit(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)))

    } match {
      case Failure(exception) =>
        log.error(s"Caught exception while converting record # $offset of topic partition $topicPartition. Stack trace: ", exception)
        FailedRecordOperation(record, exception)
      case Success(value) =>
        log.info(s"Successfully converted record # $offset of topic partition $topicPartition")
        SuccessfulConversion(record, value)
    }
  }

  /**
   * Create a [[IngestionLogRecord]] from an instance of [[RecordOperation]]
   * @param recordOperation either a [[FailedRecordOperation]] or a [[SuccessfulConversion]]
   * @return instance of [[IngestionLogRecord]]
   */

  private def getLogRecordFrom(recordOperation: RecordOperation): IngestionLogRecord =
    IngestionLogRecord(sparkSession, recordOperation, yarnUiUrl)

  /**
   * Write given instances of [[IngestionLogRecord]]
   * @param records collection of [[IngestionLogRecord]]
   */

  private def writeLogRecords(records: Seq[IngestionLogRecord]): Unit = {

    import sparkSession.implicits._

    val recordsDescription = s"${records.size} ${classOf[IngestionLogRecord].getSimpleName}(s)"
    log.info(s"Saving $recordsDescription to table $streamingLogTable")
    Try {

      val recordsDataFrame: DataFrame = records.toDF()
        .coalesce(1)
        .withSqlNamingConvention()

      super.insertInto(recordsDataFrame, streamingLogTable, SaveMode.Append)
    } match {
      case Failure(exception) => log.error(s"Caught exception while saving $recordsDescription. Stack trace: ", exception)
      case Success(_) => log.info(s"Successfully saved all of $recordsDescription")
    }
  }

  /**
   * Convert an instance of deserialized data to a [[DataFrame]]
   * @param payload instance of deserialized date
   * @return a [[DataFrame]]
   */

  protected def toDataFrame(payload: T): DataFrame

}

object StreamingJob {

  // Function for giornoGas computation
  val gasDay: Timestamp => String = ts => {

    Try {
      val zonedDateTime: ZonedDateTime = ts.toLocalDateTime.atZone(ZoneId.systemDefault)
      val dstNormalizedDateTime: ZonedDateTime = if (ZoneId.systemDefault.getRules.isDaylightSavings(zonedDateTime.toInstant))
        zonedDateTime.minusHours(1) else zonedDateTime
      if (dstNormalizedDateTime.getHour < 6)
        dstNormalizedDateTime.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
      else dstNormalizedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
    } match {
      case Failure(_) => null
      case Success(value) => value
    }
  }
}
