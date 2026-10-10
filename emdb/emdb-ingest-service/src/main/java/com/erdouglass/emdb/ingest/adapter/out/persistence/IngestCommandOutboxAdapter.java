package com.erdouglass.emdb.ingest.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.out.IngestCommandOutbox;
import com.erdouglass.emdb.ingest.messaging.IngestCommand;

@ApplicationScoped
class IngestCommandOutboxAdapter implements IngestCommandOutbox {
  
  @Inject
  JakartaDataCommandOutboxRepository commands;

  @Override
  public void save(IngestCommand command) {
    var entity = new IngestCommandOutboxEntity();
    entity.setId(command.id().value());
    entity.setIngestId(command.correlationId().value());
    entity.setSubmittedAt(command.submittedAt());
    entity.setTmdbId(command.tmdbId().value());
    entity.setMediaType(command.mediaType());
    commands.insert(entity);
  }
}
