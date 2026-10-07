package com.erdouglass.emdb.media.movie.adapter.out.messaging;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.messaging.MediaEvent;
import com.erdouglass.emdb.media.movie.application.port.out.MediaEventPublisher;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

@ApplicationScoped
class MovieEventProducer implements MediaEventPublisher {
  private static final Logger LOGGER = Logger.getLogger(MovieEventProducer.class);
  
  @Inject
  @Channel("media-events")
  MutinyEmitter<MediaEvent> emitter;

  @Override
  public void publish(MediaEvent event) {
    var metadata = OutgoingKafkaRecordMetadata.<UUID>builder()
        .withKey(event.messageId().value())
        .build();
    emitter.sendMessageAndAwait(Message.of(event).addMetadata(metadata));
    LOGGER.infof("Published: %s", event);
  }
}
