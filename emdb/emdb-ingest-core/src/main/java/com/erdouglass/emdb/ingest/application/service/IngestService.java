package com.erdouglass.emdb.ingest.application.service;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.IngestMediaCommand;
import com.erdouglass.emdb.ingest.IngestMediaUseCase;
import com.erdouglass.emdb.ingest.application.port.out.MovieRepository;
import com.erdouglass.emdb.ingest.application.port.out.PersonRepository;
import com.erdouglass.emdb.ingest.domain.model.Ingest;

@ApplicationScoped
class IngestService implements IngestMediaUseCase {
  private static final Logger LOGGER = Logger.getLogger(IngestService.class);
  
  @Inject
  MovieRepository movies;
  
  @Inject
  PersonRepository people;
  
  @Override
  public UUID ingest(IngestMediaCommand command) {
    var ingest = Ingest.submit(command.tmdbId(), command.ingestType());
    LOGGER.infof("ingest: %s", ingest);
    
    switch (command.ingestType()) {
      case MOVIE -> movies.save();
      case PERSON -> people.save();
      case SERIES -> { }
    }
    return ingest.id().value();
  }
}
