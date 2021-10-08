package it.luca.disp.streaming.core.operation

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.sql.DataFrame

case class ConversionOutput(record: ConsumerRecord[String, String],
                            throwableOrResult: Either[Throwable, DataFrame]) {

  def isFailure: Boolean = throwableOrResult.isLeft

  def isSuccess: Boolean = throwableOrResult.isRight
}
