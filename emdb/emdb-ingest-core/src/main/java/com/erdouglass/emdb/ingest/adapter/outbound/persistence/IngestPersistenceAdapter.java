package com.erdouglass.emdb.ingest.adapter.outbound.persistence;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.ingest.application.port.outbound.IngestRepository;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.IngestSource;
import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.SourceId.Source;

@ApplicationScoped
class IngestPersistenceAdapter implements IngestRepository {
  
  @Inject
  JakartaDataIngestRepository repository;

  @Override
  @Transactional
  public void save(Ingest ingest) {
    repository.save(toIngestEntity(ingest));
  }

  @Override
  @Transactional
  public Optional<Ingest> findById(IngestId id) {
    return repository.findById(id.value())
        .map(IngestPersistenceAdapter::toIngest);
  }
  
  private IngestEntity toIngestEntity(Ingest ingest) {
    IngestEntity entity = new IngestEntity();
    entity.setId(ingest.id().value());
    entity.setSource(ingest.source().provider().toString());
    entity.setSourceId(ingest.source().id());
    entity.setStatus(ingest.status());
    entity.setSubmittedAt(ingest.submittedAt());
    entity.setType(ingest.source().type());
    entity.setMessage(ingest.message());
    return entity;
  }
  
  private static Ingest toIngest(IngestEntity entity) {
    return Ingest.builder()
        .id(IngestId.of(entity.getId()))
        .source(IngestSource
            .of(SourceId.of(Source.from(entity.getSource()), entity.getSourceId()), entity.getType()))
        .status(entity.getStatus())
        .submittedAt(entity.getSubmittedAt())
        .message(entity.getMessage())
        .build();
  }
}
