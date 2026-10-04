package com.erdouglass.emdb.media.movie.adapter.out.messaging;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.Correlation;
import com.erdouglass.emdb.media.messaging.MediaSavedMessage;
import com.erdouglass.emdb.media.movie.application.port.out.MediaPublisher;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

@ApplicationScoped
class MediaAdapter implements MediaPublisher {
  
  @Inject
  @Channel("media-saved")
  MutinyEmitter<MediaSavedMessage> emitter;

  @Override
  public void publish(MediaSavedMessage event, Correlation correlation) {
    var metadata = OutgoingKafkaRecordMetadata.<UUID>builder()
        .withKey(event.id().value())
        .withHeaders(correlation.toHeaders()) 
        .build();
    emitter.sendMessageAndAwait(Message.of(event).addMetadata(metadata));
  }
}
