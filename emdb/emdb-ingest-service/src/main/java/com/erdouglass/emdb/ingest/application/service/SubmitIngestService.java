package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.in.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.shared.kernel.PublicId;

@ApplicationScoped
class SubmitIngestService implements SubmitIngestUseCase {
  
  @Inject
  Event<IngestEvent> emitter;
  
  @Inject
  IngestRepository jobs;

  @Override
  public PublicId submit(IngestMediaCommand command) {
    var job = Ingest.submit(command.tmdbId(), command.mediaType());
    jobs.save(job);
    job.pullEvents().forEach(emitter::fire);
    return job.id();
  }
}
