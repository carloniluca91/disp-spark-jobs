package it.luca.disp.streaming.app.consumer

import it.luca.disp.streaming.app.job.ConduzioneJob
import it.luca.disp.streaming.core.job.Consumer
import it.luca.disp.streaming.model.conduzione.ConduzionePayload
import org.apache.kafka.clients.consumer.KafkaConsumer

class ConduzioneConsumer(override protected val kafkaConsumer: KafkaConsumer[String, String],
                         override protected val streamingJob: ConduzioneJob)
  extends Consumer[ConduzionePayload](kafkaConsumer, streamingJob){

}
