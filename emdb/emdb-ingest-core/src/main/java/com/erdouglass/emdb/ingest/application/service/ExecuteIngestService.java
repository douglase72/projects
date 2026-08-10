package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.inbound.ExecuteIngestUseCase;
import com.erdouglass.emdb.ingest.application.port.outbound.IngestRepository;
import com.erdouglass.emdb.ingest.domain.exception.IngestNotFoundException;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

@ApplicationScoped
class ExecuteIngestService implements ExecuteIngestUseCase {
  private static final Logger LOGGER = Logger.getLogger(ExecuteIngestService.class);
  
  @Inject
  MovieIngestService movies;
  
  @Inject
  IngestRepository repository;
  
  @Override
  public void execute(IngestId id) {
    var ingest = repository.findById(id)
        .orElseThrow(() -> new IngestNotFoundException(id.value().toString()));
    
    try {
      ingest.started();
      LOGGER.info(ingest.lastEvent().message());
      repository.save(ingest);
      
      switch (ingest.type()) {
        case MOVIE -> movies.execute(ingest);
        case PERSON -> throw new UnsupportedOperationException();
        case SERIES -> throw new UnsupportedOperationException();
      }      
      
      ingest.completed();
      LOGGER.info(ingest.lastEvent().message());
      repository.save(ingest);
    } catch (Exception e) {
      ingest.failed();
      LOGGER.errorf(e, ingest.lastEvent().message());
      repository.save(ingest);
      throw e;
    }
  }
}
