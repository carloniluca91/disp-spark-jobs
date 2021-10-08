package it.luca.disp.streaming.app.consumer

import it.luca.disp.streaming.app.job.Int002Job
import it.luca.disp.streaming.core.job.Consumer
import it.luca.disp.streaming.model.int002.Int002Payload
import org.apache.kafka.clients.consumer.KafkaConsumer

class Int002Consumer(override protected val kafkaConsumer: KafkaConsumer[String, String],
                     override protected val streamingJob: Int002Job)
  extends Consumer[Int002Payload](kafkaConsumer, streamingJob){

}
