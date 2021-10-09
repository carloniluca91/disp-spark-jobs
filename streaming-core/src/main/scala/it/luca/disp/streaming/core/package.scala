package it.luca.disp.streaming

import org.apache.kafka.clients.consumer.ConsumerRecord

package object core {

  type StringConsumerRecord = ConsumerRecord[String, String]
}
