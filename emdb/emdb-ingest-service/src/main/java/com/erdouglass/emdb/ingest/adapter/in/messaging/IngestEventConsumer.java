package com.erdouglass.emdb.ingest.adapter.in.messaging;

import java.time.Duration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.ingest.application.port.in.SaveIngestEventUseCase;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.messaging.IngestEventMessage;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
class IngestEventConsumer {
  
  @Inject
  SaveIngestEventUseCase saveUseCase;

  @Blocking
  @Incoming("ingest-events")
  void onMessage(IngestEventMessage message) {
    var ingestId = IngestId.of(message.ingestId());
    var tmdbId = TmdbId.of(message.tmdbId());
    var mediaType = MediaType.from(message.mediaType());
    var event = switch (message.eventType()) {
      case SUBMITTED -> IngestSubmitted.of(ingestId, tmdbId, mediaType);
      case STARTED   -> IngestStarted.of(ingestId, tmdbId, mediaType, Duration.ZERO);
      case EXTRACTED -> IngestExtracted.of(ingestId, tmdbId, mediaType);
      default -> throw new UnsupportedOperationException();
    };
    saveUseCase.save(event);
  }
}
