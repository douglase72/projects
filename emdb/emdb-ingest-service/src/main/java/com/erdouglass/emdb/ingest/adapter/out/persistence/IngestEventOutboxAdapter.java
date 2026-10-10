package com.erdouglass.emdb.ingest.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.out.IngestEventOutbox;
import com.erdouglass.emdb.ingest.domain.event.DomainEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestCompleted;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestFailed;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.domain.model.IngestStatus;

@ApplicationScoped
class IngestEventOutboxAdapter implements IngestEventOutbox {
  
  @Inject
  JakartaDataEventOutboxRepository events;

  @Override
  public void save(DomainEvent event) {
    var entity = new IngestEventOutboxEntity();
    entity.setId(event.id().value());
    entity.setIngestId(event.ingestId().value());
    entity.setOccurredAt(event.occurredAt());
    entity.setTmdbId(event.tmdbId().value());
    entity.setMediaType(event.mediaType());
    switch (event) {
      case IngestSubmitted _ -> entity.setStatus(IngestStatus.SUBMITTED);
      case IngestStarted   _ -> entity.setStatus(IngestStatus.STARTED);
      case IngestExtracted _ -> entity.setStatus(IngestStatus.EXTRACTED);
      case IngestCompleted _ -> entity.setStatus(IngestStatus.COMPLETED);
      case IngestFailed    _ -> entity.setStatus(IngestStatus.FAILED);
    }
    events.insert(entity);
  }
}
