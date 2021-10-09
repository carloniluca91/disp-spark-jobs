package it.luca.disp.streaming.core

package object implicits {

  implicit def toRecordWrapper(record: StringConsumerRecord): StringConsumerRecordWrapper =
    new StringConsumerRecordWrapper(record)
}
