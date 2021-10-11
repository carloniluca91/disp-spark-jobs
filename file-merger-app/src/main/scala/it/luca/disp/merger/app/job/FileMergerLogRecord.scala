package it.luca.disp.merger.app.job

import it.luca.disp.core.implicits.SessionWrapper
import it.luca.disp.merger.app.dto.MergeOperationInfo

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

case class FileMergerLogRecord(applicationId: String,
                               applicationName: String,
                               applicationStartTime: Timestamp,
                               applicationStartDate: String,
                               tableName: String,
                               isPartitioned: Boolean,
                               partitionColumn: Option[String],
                               partitionValue: Option[String],
                               mergeOperationCode: String,
                               exceptionCls: Option[String],
                               exceptionMsg: Option[String],
                               yarnApplicationLogUiUrl: String,
                               yarnApplicationLogCmd: String,
                               insertTs: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
                               insertDt: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                               month: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))

object FileMergerLogRecord {

  val OK = "OK"
  val KO = "KO"

  def apply(ssWrapper: SessionWrapper,
            mergeOperationInfo: MergeOperationInfo,
            yarnUiUrl: String,
            optionalThrowable: Option[Throwable]): FileMergerLogRecord = {

    val applicationId: String = ssWrapper.applicationId
    FileMergerLogRecord(applicationId = applicationId,
      applicationName = ssWrapper.appName,
      applicationStartTime = ssWrapper.startTimeAsTimestamp,
      applicationStartDate = ssWrapper.startTimeAsString("yyyy-MM-dd"),
      tableName = mergeOperationInfo.tableName,
      isPartitioned = mergeOperationInfo.partitionColumn.isDefined && mergeOperationInfo.partitionValue.isDefined,
      partitionColumn = mergeOperationInfo.partitionColumn,
      partitionValue = mergeOperationInfo.partitionValue,
      mergeOperationCode = optionalThrowable.map(_ => KO).getOrElse(OK),
      exceptionCls = optionalThrowable.map(_.getClass.getName),
      exceptionMsg = optionalThrowable.map(_.getMessage),
      yarnApplicationLogUiUrl = s"$yarnUiUrl/$applicationId",
      yarnApplicationLogCmd = s"yarn logs -applicationId $applicationId >> $applicationId.log"
    )
  }
}
