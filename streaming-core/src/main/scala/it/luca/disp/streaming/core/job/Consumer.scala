package it.luca.disp.streaming.core.job

import org.apache.kafka.clients.consumer.KafkaConsumer

abstract class Consumer[T](protected val streaminJob: StreamingJob[T]) {
}
