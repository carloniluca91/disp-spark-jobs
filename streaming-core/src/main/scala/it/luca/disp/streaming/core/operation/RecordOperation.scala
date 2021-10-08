package it.luca.disp.streaming.core.operation

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.sql.DataFrame

sealed abstract class RecordOperation(val record: ConsumerRecord[String, String])

case class FailedRecordOperation(override val record: ConsumerRecord[String, String],
                                 throwable: Throwable)
  extends RecordOperation(record)

case class SuccessfulConversion(override val record: ConsumerRecord[String, String],
                                dataFrame: DataFrame)
  extends RecordOperation(record)
