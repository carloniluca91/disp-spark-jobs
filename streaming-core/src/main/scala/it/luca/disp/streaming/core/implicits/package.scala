package it.luca.disp.streaming.core

import org.apache.kafka.clients.consumer.ConsumerRecord

package object implicits {

  implicit def toConsumerRecordWrapper(record: ConsumerRecord[String, String]): ConsumerRecordWrapper = new ConsumerRecordWrapper(record)

}
