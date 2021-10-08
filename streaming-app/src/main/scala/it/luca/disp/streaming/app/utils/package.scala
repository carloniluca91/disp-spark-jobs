package it.luca.disp.streaming.app

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

package object utils {

  def formatDate(date: String, inputPattern: String, outputPattern: String): String =

    LocalDate.parse(date, DateTimeFormatter.ofPattern(inputPattern)).format(DateTimeFormatter.ofPattern(outputPattern))

  def toTimestamp(string: String, pattern: String): Timestamp = {

    Timestamp.valueOf(LocalDateTime.parse(string, DateTimeFormatter.ofPattern(pattern)))
  }

}
