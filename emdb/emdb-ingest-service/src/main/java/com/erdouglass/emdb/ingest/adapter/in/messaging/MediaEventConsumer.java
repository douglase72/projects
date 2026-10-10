package com.erdouglass.emdb.ingest.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.ingest.application.port.in.SaveIngestEventUseCase;
import com.erdouglass.emdb.ingest.domain.event.IngestCompleted;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.media.messaging.MediaEvent;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
class MediaEventConsumer {
  
  @Inject
  SaveIngestEventUseCase saveUseCase;
  
  @Blocking
  @Incoming("media-events")
  void onMessage(MediaEvent message) {
    var ingestId = IngestId.of(message.correlationId().value());
    var event = switch (message.eventType()) {
      case SAVED   -> IngestCompleted.of(message.id(), ingestId, message.tmdbId(), message.mediaType());
      case UPDATED -> throw new IllegalStateException();
      case DELETED -> throw new IllegalStateException();
    };
    saveUseCase.save(event);
  }
}
