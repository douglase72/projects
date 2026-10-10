package com.erdouglass.emdb.ingest.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.ingest.application.port.in.SaveIngestEventUseCase;
import com.erdouglass.emdb.ingest.domain.event.IngestCompleted;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestFailed;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.messaging.IngestEvent;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
class IngestEventConsumer {
  
  @Inject
  SaveIngestEventUseCase saveUseCase;

  @Blocking
  @Incoming("ingest-events-in")
  void onMessage(IngestEvent message) {
    var ingestId = IngestId.of(message.correlationId().value());
    var event = switch (message.eventType()) {
      case SUBMITTED -> IngestSubmitted.of(message.id(), ingestId, message.tmdbId(), message.mediaType());
      case STARTED   -> IngestStarted.of(message.id(), ingestId, message.tmdbId(), message.mediaType());
      case EXTRACTED -> IngestExtracted.of(message.id(), ingestId, message.tmdbId(), message.mediaType());
      case COMPLETED -> IngestCompleted.of(message.id(), ingestId, message.tmdbId(), message.mediaType());
      case FAILED    -> IngestFailed.of(message.id(), ingestId, message.tmdbId(), message.mediaType());
    };
    saveUseCase.save(event);
  }
}
