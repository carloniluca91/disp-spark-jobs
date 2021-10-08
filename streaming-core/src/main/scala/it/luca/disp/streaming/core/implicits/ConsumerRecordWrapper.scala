package it.luca.disp.streaming.core.implicits

import org.apache.kafka.clients.consumer.ConsumerRecord

import java.sql.Timestamp
import java.time.Instant
import java.time.format.DateTimeFormatter

class ConsumerRecordWrapper(protected val record: ConsumerRecord[String, String]) {

  def date: String = sqlTimestamp.toLocalDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE)

  def sqlTimestamp: Timestamp = Timestamp.from(Instant.ofEpochMilli(record.timestamp()))
}
