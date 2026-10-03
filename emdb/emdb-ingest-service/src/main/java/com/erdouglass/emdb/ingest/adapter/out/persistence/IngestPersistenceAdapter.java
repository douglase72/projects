package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.domain.event.IngestStarted;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

@ApplicationScoped
class IngestPersistenceAdapter implements IngestRepository {
  
  @Inject
  JakartaDataIngestRepository jobs;

  @Override
  @Transactional
  public void save(Ingest job) {
    var entity = new IngestEntity();
    entity.setId(job.id().value());
    entity.setTmdbId(job.tmdbId().value());   
    entity.setMediaType(job.mediaType());
    entity.setSubmittedAt(job.submittedAt().toInstant());
    entity.setStatus(job.status());
    jobs.save(entity);
    jobs.insert(toIngestStatusEntity(job));
  }

  @Override
  @Transactional
  public Optional<Ingest> findById(IngestId id) {
    return jobs.findById(id.value()).map(this::toIngest);
  }
  
  private Ingest toIngest(IngestEntity entity) {
    var id = IngestId.of(entity.getId());
    var tmdbId = TmdbId.of(entity.getTmdbId());
    var submittedAt = DateTimeFactory.from(entity.getSubmittedAt());
    return Ingest.rehydrate(id, tmdbId, entity.getMediaType(), submittedAt, entity.getStatus());
  }
  
  private IngestStatusEntity toIngestStatusEntity(Ingest job) {
    var event = job.events().getLast();
    var entity = new IngestStatusEntity();
    entity.setIngestId(job.id().value());
    entity.setOccurredAt(event.occurredAt());
    entity.setStatus(job.status());
    if (event instanceof IngestStarted e) {
      entity.setQueueDuration(e.queued());
    }
    return entity;
  }
}
