package it.luca.disp.streaming.core.implicits

import it.luca.disp.streaming.core.StringConsumerRecord

import java.sql.Timestamp
import java.time.Instant
import java.time.format.DateTimeFormatter

class StringConsumerRecordWrapper(protected val record: StringConsumerRecord) {

  /**
   * Get record's date
   * @return record's date with pattern yyyy-MM-dd
   */

  def getRecordDate: String = getRecordTimestamp
    .toLocalDateTime
    .format(DateTimeFormatter.ISO_LOCAL_DATE)

  /**
   * Get record's timestamp
   * @return record's timestamp as [[Timestamp]]
   */

  def getRecordTimestamp: Timestamp = Timestamp
    .from(Instant.ofEpochMilli(record.timestamp()))

}
