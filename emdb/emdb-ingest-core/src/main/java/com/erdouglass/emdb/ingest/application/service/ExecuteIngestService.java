package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.in.ExecuteIngestUseCase;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

@ApplicationScoped
class ExecuteIngestService implements ExecuteIngestUseCase {
  private static final Logger LOGGER = Logger.getLogger(ExecuteIngestService.class);
  
  @Inject
  MovieIngestService movies;
  
  @Inject
  PersonIngestService people;
  
  @Override
  public void ingest(IngestId id) {
    LOGGER.infof("Ingest started: %s", id);
    
    people.ingest();
  }
}
