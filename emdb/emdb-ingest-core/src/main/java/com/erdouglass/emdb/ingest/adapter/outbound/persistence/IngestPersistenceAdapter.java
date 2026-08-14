package com.erdouglass.emdb.ingest.adapter.outbound.persistence;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.ingest.application.port.outbound.IngestRepository;
import com.erdouglass.emdb.ingest.domain.event.IngestCompletedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestExtractedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestFailedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestLoadedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestStartedEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmittedEvent;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.IngestStatus;
import com.erdouglass.emdb.media.TmdbId;

@Transactional
@ApplicationScoped
class IngestPersistenceAdapter implements IngestRepository {
  
  @Inject
  JakartaDataIngestRepository repository;
  
  @Override
  public void save(Ingest ingest) {
    repository.save(toIngestEntity(ingest));
    repository.insert(toIngestEventEntity(ingest.lastEvent()));
  }

  @Override
  public Optional<Ingest> findById(IngestId id) {
    return repository.findById(id.value()).map(this::toIngest);
  }
  
  private IngestEntity toIngestEntity(Ingest ingest) {
    IngestEntity entity = new IngestEntity();
    entity.setId(ingest.id().value());
    entity.setTmdbId(ingest.tmdbId().value());
    entity.setType(ingest.type());
    entity.setStatus(ingest.status());
    entity.setSubmittedAt(ingest.submittedAt());
    return entity;
  }
  
  private Ingest toIngest(IngestEntity entity) {
    return Ingest.builder()
        .id(IngestId.of(entity.getId()))
        .tmdbId(TmdbId.of(entity.getTmdbId()))
        .type(entity.getType())
        .status(entity.getStatus())
        .submittedAt(entity.getSubmittedAt())
        .build();
  }
  
  private IngestEventEntity toIngestEventEntity(IngestEvent event) {
    var status = switch (event) {
      case IngestSubmittedEvent _ -> IngestStatus.SUBMITTED;
      case IngestStartedEvent   _ -> IngestStatus.STARTED;
      case IngestExtractedEvent _ -> IngestStatus.EXTRACTED;
      case IngestLoadedEvent    _ -> IngestStatus.LOADED;
      case IngestCompletedEvent _ -> IngestStatus.COMPLETED;
      case IngestFailedEvent    _ -> IngestStatus.FAILED;
    };
    var entity = new IngestEventEntity();
    entity.setIngestId(event.id().value());
    entity.setOccurredAt(event.occurredAt());
    entity.setStatus(status);
    entity.setMessage(event.message());
    return entity;
  }
}
