package it.luca.disp.streaming.core.job

import it.luca.disp.core.BaseTestSuiteWithMocking
import it.luca.disp.core.implicits.SparkSessionWrapper
import it.luca.disp.streaming.core.StringConsumerRecord
import it.luca.disp.streaming.core.operation.{FailedRecordOperation, SuccessfulConversion}
import org.apache.kafka.common.record.TimestampType

import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime}

class IngestionLogRecordTest
  extends BaseTestSuiteWithMocking {

  protected final val offset: Long = 300
  protected final val (topicName, topicPartition): (String, Int) = ("topicName", 1)
  protected final val recordTimestampLong: Long = System.currentTimeMillis()
  protected final val recordTimestampTs: Timestamp = Timestamp.from(Instant.ofEpochMilli(recordTimestampLong))
  protected final val consumerRecord: StringConsumerRecord = new StringConsumerRecord(topicName,
      topicPartition,
      offset,
      recordTimestampLong,
      TimestampType.CREATE_TIME,
      0,
      0,
      0,
      null,
      null)

  private val (appId, appName) = ("appId", "appName")
  private val startTime: LocalDateTime = LocalDateTime.now()
  private val ssWrapper: SparkSessionWrapper = stub[SparkSessionWrapper]
  (ssWrapper.applicationId _).when().returns(appId)
  (ssWrapper.appName _).when().returns(appName)
  (ssWrapper.startTimeAsTimestamp _).when().returns(Timestamp.valueOf(startTime))
  (ssWrapper.startTimeAsString _).when(IngestionLogRecord.StartDatePattern)
    .returns(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE))

  private val yarnUiUrl = "https://yarn/ui/url"

  s"A ${nameOf[IngestionLogRecord]}" should
    s"be correctly initialized by an instance of ${nameOf[SuccessfulConversion]}" in {

    val successfulConversion = SuccessfulConversion(consumerRecord, null)
    val record = IngestionLogRecord(ssWrapper, successfulConversion, yarnUiUrl)
    record.recordTs shouldBe recordTimestampTs
    record.recordDt shouldBe recordTimestampTs.toLocalDateTime
      .format(DateTimeFormatter.ISO_LOCAL_DATE)

    record.recordTopic shouldBe topicName
    record.recordPartition shouldBe topicPartition
    record.recordOffset shouldBe offset
    record.ingestionOperationCode shouldBe IngestionLogRecord.OK
    record.exceptionCls shouldBe None
    record.exceptionMsg shouldBe None
    record.applicationId shouldBe appId
    record.applicationName shouldBe appName
    record.applicationStartTime shouldBe Timestamp.valueOf(startTime)
    record.applicationStartDate shouldBe startTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
  }

  it should s"be correctly initialized by an instance of ${nameOf[FailedRecordOperation]}" in {

    val throwableMsg = "throwableMessage!"
    val throwable: Throwable = new IllegalArgumentException(throwableMsg)
    val failedRecordOperation = FailedRecordOperation(consumerRecord, throwable)
    val record = IngestionLogRecord(ssWrapper, failedRecordOperation, yarnUiUrl)

    record.exceptionCls shouldBe Some(throwable.getClass.getName)
    record.exceptionMsg shouldBe Some(throwableMsg)
  }
}
