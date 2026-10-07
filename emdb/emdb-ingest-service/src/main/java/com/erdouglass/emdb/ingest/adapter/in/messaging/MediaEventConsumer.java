package com.erdouglass.emdb.ingest.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.ingest.application.port.in.SaveIngestEventUseCase;
import com.erdouglass.emdb.ingest.domain.event.IngestCompleted;
import com.erdouglass.emdb.media.messaging.MediaEvent;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
class MediaEventConsumer {
  
  @Inject
  SaveIngestEventUseCase saveUseCase;

  @Blocking
  @Incoming("media-events")
  void onMessage(MediaEvent message) {
    var messageId = message.messageId();
    var correlationId = message.correlationId();
    var tmdbId = message.tmdbId();
    var mediaType = message.mediaType();
    var event = switch (message.eventType()) {
      case SAVED   -> IngestCompleted.of(messageId, correlationId, tmdbId, mediaType);
      case UPDATED -> throw new IllegalArgumentException();
      case DELETED -> throw new IllegalArgumentException();
    };
    saveUseCase.save(event);
  }
}
