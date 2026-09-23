package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestJobRepository;
import com.erdouglass.emdb.ingest.application.port.out.IngestPublisher;
import com.erdouglass.emdb.ingest.domain.event.IngestEvent;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.IngestJob;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

@ApplicationScoped
class SubmitIngestService implements SubmitIngestUseCase {
  private static final Logger LOGGER = Logger.getLogger(SubmitIngestService.class);
  
  @Inject
  Event<IngestEvent> emitter;
  
  @Inject
  IngestJobRepository jobs;
  
  @Inject
  IngestPublisher publisher;

  @Override
  public IngestId submit(IngestMediaCommand command) {
    var job = IngestJob.submit(TmdbId.of(command.tmdbId()), command.ingestType());
    jobs.save(job);
    
    try {
      publisher.publish(job.id());
      emitter.fire(job.events().getLast());
    } catch (Exception e) {
      LOGGER.errorf(e, "Failed to publish command: %s", command);
      throw e;
    }
    return job.id();
  }
}
