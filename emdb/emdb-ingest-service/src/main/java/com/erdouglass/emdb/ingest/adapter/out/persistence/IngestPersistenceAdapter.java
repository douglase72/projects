package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.application.port.out.IngestEventRepository;
import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.domain.event.DomainEvent;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.event.IngestSubmitted;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.IngestStatus;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.TmdbId;

@ApplicationScoped
class IngestPersistenceAdapter implements IngestRepository, IngestEventRepository {
  
  @Inject
  JakartaDataIngestEventRepository events;
  
  @Inject
  JakartaDataIngestRepository jobs;

  @Override
  public void save(Ingest job) {
    var entity = new IngestEntity();
    entity.setId(job.id().value());
    entity.setTmdbId(job.tmdbId().value());   
    entity.setMediaType(job.mediaType());
    entity.setSubmittedAt(job.submittedAt().toInstant());
    entity.setStatus(job.status());
    jobs.save(entity);
    events.insert(toIngestEventEntity(job.events().getLast()));
  }

  @Override
  public Optional<Ingest> findById(IngestId id) {
    return jobs.findById(id.value()).map(this::toIngest);
  }
  
  @Override
  public boolean existsById(MessageId id) {
    return events.findById(id.value()) != null;
  }
  
  private Ingest toIngest(IngestEntity entity) {
    var id = IngestId.of(entity.getId());
    var tmdbId = TmdbId.of(entity.getTmdbId());
    var submittedAt = DateTimeFactory.from(entity.getSubmittedAt());
    var mediaType = entity.getMediaType();
    var domainEvents = events.findAll().stream().map(e -> toDomainEvent(e, tmdbId, mediaType)).toList();
    return Ingest.rehydrate(id, tmdbId, mediaType, submittedAt, entity.getStatus(), domainEvents);
  }
  
  private IngestEventEntity toIngestEventEntity(DomainEvent event) {
    var entity = new IngestEventEntity();
    entity.setId(event.id().value());
    entity.setIngestId(event.ingestId().value());
    entity.setOccurredAt(event.occurredAt());
    entity.setTmdbId(event.tmdbId().value());
    entity.setMediaType(event.mediaType());
    switch (event) {
      case IngestSubmitted _ -> entity.setStatus(IngestStatus.SUBMITTED);
      case IngestStarted   _ -> entity.setStatus(IngestStatus.STARTED);
    }
    return entity;
  }
  
  private DomainEvent toDomainEvent(IngestEventEntity entity, TmdbId tmdbId, MediaType mediaType) {
    var messageId = MessageId.of(entity.getId());
    var ingestId = IngestId.of(entity.getIngestId());
    return switch (entity.getStatus()) {
      case SUBMITTED -> IngestSubmitted.of(messageId, ingestId, tmdbId, mediaType);
      case STARTED   -> IngestStarted.of(messageId, ingestId, tmdbId, mediaType);
      default -> throw new IllegalArgumentException("Invalid status: " + entity.getStatus());
    };
  }
}
