package it.luca.disp.streaming.core.job

import it.luca.disp.core.Logging
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition

import java.time.Duration
import java.util.Collections

import scala.collection.JavaConversions._

/**
 * Base class to extend in order to implement a [[KafkaConsumer]] that exploits a
 * concrete instance of a [[StreamingJob]] for data processing and storage
 * @param kafkaConsumer [[KafkaConsumer]] to be used for polling records from Kafka
 * @param streamingJob instance of [[StreamingJob]] that processes polled Kafka records
 * @tparam T type of consumed data
 */

abstract class Consumer[T](protected val kafkaConsumer: KafkaConsumer[String, String],
                           protected val streamingJob: StreamingJob[T])
  extends Logging {

  protected val topicPartition: TopicPartition = kafkaConsumer.assignment().head

  /**
   * Poll data from Kafka
   * @return true if the consumer polled some data, false otherwise
   */

  def poll(): Boolean = {

    // Poll data from Kafka
    log.info(s"Starting to poll records from topic partition $topicPartition")
    val batch: ConsumerRecords[String, String] = kafkaConsumer.poll(Duration.ofSeconds(10))
    if (batch.isEmpty) {
      log.info("No records polled from topic partition {}", topicPartition)
      false
    } else {

      // Convert batch of polled dara to a list of ConsumerRecord(s) and process them
      val consumerRecords: Seq[ConsumerRecord[String, String]] = batch.iterator().toSeq
      val optionalOffset: Option[Long] = streamingJob.processBatch(consumerRecords)
      if (optionalOffset.isDefined) {

        // Commit offset, if it has been returned by the streaming job
        val offsetToCommit: Long = optionalOffset.get
        kafkaConsumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(offsetToCommit + 1)))
        log.info(s"Successfully committed offset $offsetToCommit for topic partition $topicPartition")
      }

      true
    }
  }
}
