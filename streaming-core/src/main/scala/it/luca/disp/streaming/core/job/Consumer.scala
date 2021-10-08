package it.luca.disp.streaming.core.job

import it.luca.disp.core.Logging
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition

import java.time.Duration
import java.util.Collections

import scala.collection.JavaConversions._

abstract class Consumer[T](protected val kafkaConsumer: KafkaConsumer[String, String],
                           protected val streamingJob: StreamingJob[T])
  extends Logging {

  protected val topicPartition: TopicPartition

  def poll(): Boolean = {

    log.info(s"Starting to poll records from topic partition $topicPartition")
    val batch: ConsumerRecords[String, String] = kafkaConsumer.poll(Duration.ofSeconds(10))
    if (batch.isEmpty) {
      log.info("No records polled from topic partition {}", topicPartition)
      false
    } else {

      // Convert batch to a list of ConsumerRecord(s)
      val consumerRecords: Seq[ConsumerRecord[String, String]] = batch.iterator().toSeq
      val optionalOffset: Option[Long] = streamingJob.processBatch(consumerRecords)
      if (optionalOffset.isDefined) {

        val offsetToCommit: Long = optionalOffset.get
        kafkaConsumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(offsetToCommit + 1)))
        log.info(s"Successfully committed offset $offsetToCommit for topic partition $topicPartition")
      }

      true
    }
  }
}
