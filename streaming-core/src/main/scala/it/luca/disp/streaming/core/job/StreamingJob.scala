package it.luca.disp.streaming.core.job

import it.luca.disp.core.Logging
import it.luca.disp.core.implicits._
import it.luca.disp.core.job.SparkJob
import it.luca.disp.streaming.core.ObjectDeserializer.deserializeAsMsgWrapper
import it.luca.disp.streaming.core.implicits._
import it.luca.disp.streaming.core.operation.{FailedRecordOperation, RecordOperation, SuccessfulConversion}
import it.luca.disp.streaming.model.MsgWrapper
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.sql.{Connection, Timestamp}
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import scala.util.{Failure, Success, Try}

abstract class StreamingJob[T](override protected val sparkSession: SparkSession,
                               override protected val impalaConnection: Connection,
                               protected val properties: PropertiesConfiguration,
                               protected val tClass: Class[T])
  extends SparkJob(sparkSession, impalaConnection)
    with Logging {

  protected val targetTable: String
  protected val saveMode: SaveMode

  protected final val streamingLogTable: String = properties.getString("spark.log.table")
  protected final val yarnUiUrl: String = properties.getString("yarn.logs.ui.url")

  protected def buildLogRecord(record: ConsumerRecord[String, String], optionalThrowable: Option[Throwable]): IngestionLogRecord =
    IngestionLogRecord(sparkSession, record, optionalThrowable, yarnUiUrl)

  def processBatchOfMessages(records: Seq[ConsumerRecord[String, String]]): Option[Long] = {

    val conversionOutputs: Seq[RecordOperation] = records.map(processMessage)
    val logRecordsFromFailedConversions: Seq[IngestionLogRecord] = conversionOutputs.collect {
      case FailedRecordOperation(record, throwable) => buildLogRecord(record, Some(throwable))
    }
    val successfulConversions: Seq[SuccessfulConversion] = conversionOutputs.collect { case s: SuccessfulConversion => s }
    val optionalOffsetAndLogRecords: (Option[Long], Seq[IngestionLogRecord]) = if (successfulConversions.isEmpty) {
      log.error("None of the consumer record(s) from this batch has been successfully converted")
      (None, Seq.empty[IngestionLogRecord])
    } else {

      val dataFrame: DataFrame = successfulConversions.map { _.dataFrame }.reduce { _ union _}
      log.info(s"Successfully reduced all of ${successfulConversions.size} ${classOf[DataFrame].getSimpleName}(s)")
      Try { super.insertInto(dataFrame, targetTable, saveMode) } match {
        case Failure(exception) => (None, successfulConversions.map { x => buildLogRecord(x.record, Some(exception)) })
        case Success(_) =>
          val successfullyWrittenRecords: Seq[ConsumerRecord[String, String]] = successfulConversions.map { _.record }
          (Some(successfullyWrittenRecords.map { _.offset()}.max),
          successfullyWrittenRecords.map { x => buildLogRecord(x, None)})
      }
    }

    val (optionalOffset, secondSetOfLogRecords) = optionalOffsetAndLogRecords
    this.writeLogRecords(logRecordsFromFailedConversions ++ secondSetOfLogRecords)
    optionalOffset
  }

  protected def processMessage(record: ConsumerRecord[String, String]): RecordOperation = {

    val (topic, partition, offset): (String, Int, Long) = (record.topic, record.partition, record.offset)
    val topicPartition: String = s"$topic-$partition"
    log.info(s"Converting record # $offset of topic partition $topicPartition")
    Try {

      val msgWrapper: MsgWrapper[T] = deserializeAsMsgWrapper(record.value, tClass)
      toDataFrame(msgWrapper)
        .withColumn("record_offset", lit(offset))
        .withColumn("record_topic", lit(topic))
        .withColumn("record_partition", lit(partition))
        .withColumn("record_ts", lit(record.sqlTimestamp))
        .withColumn("record_dt", lit(record.date))
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

  protected def writeLogRecords(records: Seq[IngestionLogRecord]): Unit = {

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

  protected def toDataFrame(instance: MsgWrapper[T]): DataFrame

}
