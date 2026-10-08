package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.ingest.application.port.in.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

@ApplicationScoped
class SubmitIngestService implements SubmitIngestUseCase {
  
  @Inject
  IngestRepository jobs;

  /// Publish the command to the broker.
  /// 
  /// Commit the [Ingest] job and the [IngestMediaCommand] in the same transaction
  /// to avoid a duel write.
  @Override
  @Transactional
  public IngestId submit(IngestMediaCommand command) {
    var job = Ingest.submit(command.tmdbId(), command.mediaType());
    jobs.save(job);
    return job.id();
  }
}
