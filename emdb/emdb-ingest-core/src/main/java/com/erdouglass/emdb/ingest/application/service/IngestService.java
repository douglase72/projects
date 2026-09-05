package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.in.ExecuteIngestUseCase;
import com.erdouglass.emdb.ingest.application.port.in.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.application.port.out.IngestEmitter;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

@ApplicationScoped
class IngestService implements SubmitIngestUseCase, ExecuteIngestUseCase {
  private static final Logger LOGGER = Logger.getLogger(IngestService.class);
  
  @Inject
  IngestEmitter emitter;
  
  @Inject
  PersonIngestService people;

  @Override
  public IngestId submit(IngestMediaCommand command) {
    var ingest = Ingest.submit(command.tmdbId(), command.type());
    emitter.publish(ingest.id());
    LOGGER.debugf("Submitted: %s", ingest);
    return ingest.id();
  }

  @Override
  public void ingest(IngestId id) {
    LOGGER.infof("Ingest started: %s", id);
    people.ingest();
  }
}
