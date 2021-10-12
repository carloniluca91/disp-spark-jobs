package it.luca.disp.merger.app.job

import it.luca.disp.core.BaseTestSuiteWithMocking
import it.luca.disp.core.implicits.SessionWrapper
import org.apache.spark.sql.catalog.{Column, Table}

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MergerLogRecordTest
  extends BaseTestSuiteWithMocking {

  private val (appId, appName) = ("appId", "appName")
  private val startTime: LocalDateTime = LocalDateTime.now()
  private val ssWrapper: SessionWrapper = stub[SessionWrapper]
  (ssWrapper.applicationId _).when().returns(appId)
  (ssWrapper.appName _).when().returns(appName)
  (ssWrapper.startTimeAsTimestamp _).when().returns(Timestamp.valueOf(startTime))
  (ssWrapper.startTimeAsString _).when(*)
    .returns(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE))

  private val yarnUiUrl = "https://yarn/ui/url"

  private val (tableName, dbName) = ("tableName", "dbName")
  private val testTable: Table = new Table(tableName, dbName, null, "MANAGED", false)
  private val (partitionColumnName, partitionColumnValue) = ("partitionColumn", "partitionValue")
  private val partitionColumn: Column = new Column(partitionColumnName, null, "string", false, true, false)

  s"A ${nameOf[MergerLogRecord]}" should "be correctly initialized in case of non-partitioned table" in {

    val record = MergerLogRecord(ssWrapper, testTable, None, yarnUiUrl)
    record.applicationId shouldBe appId
    record.applicationName shouldBe appName
    record.applicationStartTime shouldBe Timestamp.valueOf(startTime)
    record.applicationStartDate shouldBe startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
    record.tableName shouldBe tableName
    record.isPartitioned shouldBe false
    record.partitionColumn shouldBe None
    record.partitionValue shouldBe None
    record.operationCode shouldBe MergerLogRecord.OK
    record.exceptionCls shouldBe None
    record.exceptionMsg shouldBe None
  }

  it should "be correctly initialized in case of a partitioned table" in {

    val throwableMsg = "throwableMessage!"
    val throwable = new IllegalArgumentException(throwableMsg)
    val record = MergerLogRecord(ssWrapper, testTable, partitionColumn, partitionColumnValue, Some(throwable), yarnUiUrl)
    record.isPartitioned shouldBe true
    record.tableName shouldBe tableName
    record.partitionColumn shouldBe Some(partitionColumnName)
    record.partitionValue shouldBe Some(partitionColumnValue)
    record.operationCode shouldBe MergerLogRecord.KO
    record.exceptionCls shouldBe Some(throwable.getClass.getName)
    record.exceptionMsg shouldBe Some(throwableMsg)
  }
}
