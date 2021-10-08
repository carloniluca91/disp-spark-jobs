package it.luca.disp.streaming.app

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

package object utils {

  /**
   * Transform a string expressing a date from one pattern to another
   * @param date input string
   * @param inputPattern pattern of input string
   * @param outputPattern pattern for output string
   * @return input string formatted according to output pattern
   */

  def formatDate(date: String, inputPattern: String, outputPattern: String): String =

    LocalDate.parse(date, DateTimeFormatter.ofPattern(inputPattern))
      .format(DateTimeFormatter.ofPattern(outputPattern))

  /**
   * Convert a string to a [[Timestamp]]
   * @param string input string
   * @param pattern pattern of input string
   * @return instance of [[Timestamp]]
   */

  def toTimestamp(string: String, pattern: String): Timestamp = {

    Timestamp.valueOf(LocalDateTime.parse(string, DateTimeFormatter.ofPattern(pattern)))
  }

}
