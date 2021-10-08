package it.luca.disp.streaming.app.consumer

import it.luca.disp.streaming.app.job.JarvisJob
import it.luca.disp.streaming.core.job.Consumer
import it.luca.disp.streaming.model.jarvis.JarvisPayload
import org.apache.kafka.clients.consumer.KafkaConsumer

class JarvisConsumer(override protected val kafkaConsumer: KafkaConsumer[String, String],
                     override protected val streamingJob: JarvisJob)
  extends Consumer[JarvisPayload](kafkaConsumer, streamingJob){

}
