package it.luca.disp.merger.app.job

import it.luca.disp.core.implicits.SessionWrapper
import org.apache.spark.sql.catalog.{Column, Table}

import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

case class MergerLogRecord(applicationId: String,
                           applicationName: String,
                           applicationStartTime: Timestamp,
                           applicationStartDate: String,
                           tableName: String,
                           isPartitioned: Boolean,
                           partitionColumn: Option[String],
                           partitionValue: Option[String],
                           operationCode: String,
                           exceptionCls: Option[String],
                           exceptionMsg: Option[String],
                           yarnApplicationLogUiUrl: String,
                           yarnApplicationLogCmd: String,
                           insertTs: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
                           insertDt: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                           month: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))

object MergerLogRecord {

  val OK = "OK"
  val KO = "KO"

  protected def apply(ssWrapper: SessionWrapper,
                      tableName: String,
                      partitionColumn: Option[String],
                      partitionValue: Option[String],
                      optionalThrowable: Option[Throwable],
                      yarnUiUrl: String): MergerLogRecord = {

    val applicationId: String = ssWrapper.applicationId
    MergerLogRecord(applicationId = applicationId,
      applicationName = ssWrapper.appName,
      applicationStartTime = ssWrapper.startTimeAsTimestamp,
      applicationStartDate = ssWrapper.startTimeAsString("yyyy-MM-dd"),
      tableName = tableName,
      isPartitioned = partitionColumn.isDefined && partitionValue.isDefined,
      partitionColumn = partitionColumn,
      partitionValue = partitionValue,
      operationCode = optionalThrowable.map(_ => KO).getOrElse(OK),
      exceptionCls = optionalThrowable.map(_.getClass.getName),
      exceptionMsg = optionalThrowable.map(_.getMessage),
      yarnApplicationLogUiUrl = s"$yarnUiUrl/$applicationId",
      yarnApplicationLogCmd = s"yarn logs -applicationId $applicationId >> $applicationId.log"
    )
  }

  def apply(ssWrapper: SessionWrapper,
            table: Table,
            optionalThrowable: Option[Throwable],
            yarnUiUrl: String): MergerLogRecord = {

    apply(ssWrapper, table.name, None, None, optionalThrowable, yarnUiUrl)
  }

  def apply(ssWrapper: SessionWrapper,
            table: Table,
            partitionColumn: Column,
            partitionValue: String,
            optionalThrowable: Option[Throwable],
            yarnUiUrl: String): MergerLogRecord = {

    apply(ssWrapper, table.name, Some(partitionColumn.name), Some(partitionValue), optionalThrowable, yarnUiUrl)
  }
}
