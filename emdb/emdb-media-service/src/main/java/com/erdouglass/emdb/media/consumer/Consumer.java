package com.erdouglass.emdb.media.consumer;

import java.util.concurrent.CompletionStage;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;

public abstract class Consumer<T> {
  private static final Logger LOGGER = Logger.getLogger(Consumer.class);
  private static final double KB = 1024.0;
  
  public CompletionStage<Void> consume(Message<T> message) {
    LOGGER.infof("Received: %s", message.getPayload());
    message.getMetadata(IncomingKafkaRecordMetadata.class).ifPresent(m -> {
      ConsumerRecord<?, ?> record = m.getRecord();
      LOGGER.infof("topic: %s", record.topic());
      LOGGER.infof("partition: %d", record.partition());
      LOGGER.infof("offset: %d", record.offset());
      LOGGER.infof("size: %.1f KB", record.serializedValueSize() / KB);
    });
    
    try {
      process(message);
      return message.ack();
    } catch (Exception e) {
      var topic = message.getMetadata(IncomingKafkaRecordMetadata.class)
          .map(m -> m.getRecord().topic())
          .orElse("unknown");
      LOGGER.errorf("Failed to process message for topic %s: %s", topic, e.getMessage());
      return message.nack(e);
    }
  }
  
  protected abstract void process(Message<T> message);
  
}
