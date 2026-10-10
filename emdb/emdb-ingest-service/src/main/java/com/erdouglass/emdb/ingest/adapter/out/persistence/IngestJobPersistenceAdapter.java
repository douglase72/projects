package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.application.port.out.IngestEventRepository;
import com.erdouglass.emdb.ingest.application.port.out.IngestJobRepository;
import com.erdouglass.emdb.ingest.domain.event.DomainEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestCompleted;
import com.erdouglass.emdb.ingest.domain.event.IngestExtracted;
import com.erdouglass.emdb.ingest.domain.event.IngestFailed;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.IngestStatus;
import com.erdouglass.emdb.shared.kernel.TmdbId;

@ApplicationScoped
class IngestJobPersistenceAdapter implements IngestJobRepository, IngestEventRepository {
  
  @Inject
  JakartaDataIngestEventRepository events;
  
  @Inject
  JakartaDataIngestJobRepository jobs;

  @Override
  public void save(Ingest job) {
    var entity = new IngestJobEntity();
    entity.setId(job.id().value());
    entity.setTmdbId(job.tmdbId().value());   
    entity.setMediaType(job.mediaType());
    entity.setSubmittedAt(job.submittedAt().toInstant());
    entity.setStatus(job.status());
    jobs.save(entity);
    events.insert(toIngestEventEntity(job.events().getLast()));
  }
  
  @Override
  public boolean existsById(MessageId id) {
    return events.findById(id.value()).isPresent();
  }
  
  @Override
  public Optional<Ingest> findById(IngestId id) {
    return jobs.findById(id.value()).map(this::toIngest);
  }
  
  private Ingest toIngest(IngestJobEntity entity) {
    var id = IngestId.of(entity.getId());
    var tmdbId = TmdbId.of(entity.getTmdbId());
    var submittedAt = DateTimeFactory.from(entity.getSubmittedAt());
    var mediaType = entity.getMediaType();
    return Ingest.rehydrate(id, tmdbId, mediaType, submittedAt, entity.getStatus());
  }
  
  private IngestEventEntity toIngestEventEntity(DomainEvent event) {
    var entity = new IngestEventEntity();
    entity.setId(event.id().value());
    entity.setIngestId(event.ingestId().value());
    entity.setOccurredAt(event.occurredAt());
    switch (event) {
      case IngestSubmitted _ -> entity.setStatus(IngestStatus.SUBMITTED);
      case IngestStarted   _ -> entity.setStatus(IngestStatus.STARTED);
      case IngestExtracted _ -> entity.setStatus(IngestStatus.EXTRACTED);
      case IngestCompleted _ -> entity.setStatus(IngestStatus.COMPLETED);
      case IngestFailed    _ -> entity.setStatus(IngestStatus.FAILED);
    }
    return entity;
  }
}
