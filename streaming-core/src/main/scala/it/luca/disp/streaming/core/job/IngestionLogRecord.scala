package it.luca.disp.streaming.core.job

import it.luca.disp.core.implicits.SessionWrapper
import it.luca.disp.streaming.core.StringConsumerRecord
import it.luca.disp.streaming.core.implicits._
import it.luca.disp.streaming.core.dto.{FailedRecordOperation, RecordOperation, SuccessfulConversion}

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
  val StartDatePattern = "yyyy-MM-dd"

  def apply(ss: SessionWrapper,
            recordOperation: RecordOperation,
            yarnUiUrl: String): IngestionLogRecord = {

    val applicationId: String = ss.applicationId
    val (record, optionalThrowable): (StringConsumerRecord, Option[Throwable]) = recordOperation match {
      case FailedRecordOperation(record, throwable) => (record, Some(throwable))
      case SuccessfulConversion(record, _) => (record, None)
    }

    IngestionLogRecord(
      recordTs = record.getRecordTimestamp,
      recordDt = record.getRecordDate,
      recordTopic = record.topic(),
      recordPartition = record.partition(),
      recordOffset = record.offset(),
      ingestionOperationCode = optionalThrowable.map(_ => KO).getOrElse(OK),
      exceptionCls = optionalThrowable.map(_.getClass.getName),
      exceptionMsg = optionalThrowable.map(_.getMessage),
      applicationId = applicationId,
      applicationName = ss.appName,
      applicationStartTime = ss.startTimeAsTimestamp,
      applicationStartDate = ss.startTimeAsString(StartDatePattern),
      yarnApplicationLogUiUrl = s"$yarnUiUrl/$applicationId",
      yarnApplicationLogCmd = s"yarn logs -applicationId $applicationId >> $applicationId.log")
  }
}
