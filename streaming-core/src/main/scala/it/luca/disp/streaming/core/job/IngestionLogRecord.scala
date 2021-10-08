package it.luca.disp.streaming.core.job

import it.luca.disp.core.implicits.SparkSessionWrapper
import it.luca.disp.streaming.core.implicits._
import org.apache.kafka.clients.consumer.ConsumerRecord

import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

case class IngestionLogRecord(recordTs: Timestamp,
                              recordDt: String,
                              recordTopic: String,
                              recordPartition: Integer,
                              recordOffset: Long,
                              ingestionOperationCode: String,
                              exceptionCls: Option[String],
                              exceptionMsg: Option[String],
                              applicationId: String,
                              applicationName: String,
                              applicationStartTime: Timestamp,
                              applicationStartDate: String,
                              yarnApplicationLogUiUrl: String,
                              yarnApplicationLogCmd: String,
                              insertTs: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
                              insertDt: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                              month: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))

object IngestionLogRecord {

  val OK = "OK"
  val KO = "KO"

  def apply(ss: SparkSessionWrapper,
            record: ConsumerRecord[String, String],
            optionalThrowable: Option[Throwable],
            yarnUiUrl: String): IngestionLogRecord = {

    val applicationId: String = ss.applicationId
    IngestionLogRecord(
      recordTs = record.sqlTimestamp,
      recordDt = record.date,
      recordTopic = record.topic(),
      recordPartition = record.partition(),
      recordOffset = record.offset(),
      ingestionOperationCode = optionalThrowable.map(_ => KO).getOrElse(OK),
      exceptionCls = optionalThrowable.map(_.getClass.getSimpleName),
      exceptionMsg = optionalThrowable.map(_.getMessage),
      applicationId = applicationId,
      applicationName = ss.appName,
      applicationStartTime = ss.startTimeAsTimestamp,
      applicationStartDate = ss.startTimeAsString("yyyy-MM-dd"),
      yarnApplicationLogUiUrl = s"$yarnUiUrl/$applicationId",
      yarnApplicationLogCmd = s"yarn logs -applicationId $applicationId >> $applicationId.log")
  }
}
