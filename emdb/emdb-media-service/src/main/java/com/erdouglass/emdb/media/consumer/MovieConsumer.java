package com.erdouglass.emdb.media.consumer;

import java.util.concurrent.CompletionStage;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieMessage;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  private static final double KB = 1024.0;

  @Blocking
  @Incoming("movies")
  public CompletionStage<Void> ingest(Message<MovieMessage> message) {
    LOGGER.infof("Received: %s", message.getPayload());
    message.getMetadata(IncomingKafkaRecordMetadata.class).ifPresent(m -> {
      ConsumerRecord<?, ?> record = m.getRecord();
      LOGGER.infof("topic: %s", record.topic());
      LOGGER.infof("partition: %d", record.partition());
      LOGGER.infof("offset: %d", record.offset());
      LOGGER.infof("size: %.1f KB", record.serializedValueSize() / KB);
    });

    try {
      var movie = message.getPayload().movie();
      LOGGER.infof("movie: %s", movie);

      var people = message.getPayload().people();
      LOGGER.infof("people: %d", people.size());
      
      var credits = message.getPayload().credits();
      LOGGER.infof("credits: %d", credits.size());
      return message.ack();
    } catch (Exception e) {
      return message.nack(e);
    }
  }

}
