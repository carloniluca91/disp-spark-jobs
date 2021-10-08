package it.luca.disp.streaming.app.consumer

import it.luca.disp.streaming.app.job.WebdispJob
import it.luca.disp.streaming.core.job.Consumer
import it.luca.disp.streaming.model.webdisp.WebdispPayload
import org.apache.kafka.clients.consumer.KafkaConsumer

class WebdispConsumer(override protected val kafkaConsumer: KafkaConsumer[String, String],
                      override protected val streamingJob: WebdispJob)
  extends Consumer[WebdispPayload](kafkaConsumer, streamingJob)
