package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.application.port.out.IngestJobRepository;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.IngestJob;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

@ApplicationScoped
class IngestJobPersistenceAdapter implements IngestJobRepository {
  
  @Inject
  JakartaDataIngestJobRepository jobs;

  @Override
  @Transactional
  public void save(IngestJob job) {
    var entity = new IngestJobEntity();
    entity.setId(job.id().value());
    entity.setTmdbId(job.tmdbId().value());   
    entity.setIngestType(job.type());
    entity.setSubmittedAt(job.submittedAt().toInstant());
    entity.setStage(job.stage());
    jobs.save(entity);
  }

  @Override
  @Transactional
  public Optional<IngestJob> findById(IngestId id) {
    return jobs.findById(id.value()).map(this::toIngestJob);
  }
  
  private IngestJob toIngestJob(IngestJobEntity entity) {
    var id = IngestId.of(entity.getId());
    var tmdbId = TmdbId.of(entity.getTmdbId());
    var submittedAt = DateTimeFactory.from(entity.getSubmittedAt());
    return IngestJob.rehydrate(id, tmdbId, entity.getIngestType(), submittedAt, entity.getStage());
  }
}
