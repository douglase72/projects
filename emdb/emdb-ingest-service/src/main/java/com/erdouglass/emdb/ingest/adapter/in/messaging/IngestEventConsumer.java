package com.erdouglass.emdb.ingest.adapter.in.messaging;

import java.time.Duration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.ingest.application.port.in.SaveIngestEventUseCase;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.messaging.IngestEvent;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
class IngestEventConsumer {
  
  @Inject
  SaveIngestEventUseCase saveUseCase;

  @Blocking
  @Incoming("ingest-events-in")
  void onMessage(IngestEvent message) {
    var event = switch (message.eventType()) {
      case SUBMITTED -> IngestSubmitted.of(message.messageId(), message.correlationId(), message.tmdbId(), message.mediaType());
      case STARTED   -> IngestStarted.of(message.messageId(), message.correlationId(), message.tmdbId(), message.mediaType(), Duration.ZERO);
      case EXTRACTED -> IngestExtracted.of(message.messageId(), message.correlationId(), message.tmdbId(), message.mediaType());
      case COMPLETED -> throw new IllegalArgumentException();
      case FAILED    -> throw new IllegalArgumentException();
    };
    saveUseCase.save(event);
  }
}
