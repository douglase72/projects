package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.application.port.out.Media;
import com.erdouglass.emdb.ingest.application.port.out.PersonSource;
import com.erdouglass.emdb.ingest.domain.model.Ingest;

@ApplicationScoped
class PersonIngestService {
  
  @Inject
  Media media;
  
  @Inject
  IngestRepository repository;
  
  @Inject
  PersonSource source;

  public void execute(Ingest ingest) {
    
    // Extract the person details from TMDB.
    var person = source.extract(ingest.tmdbId());
    ingest.extracted();
    repository.save(ingest);
    
    // Load the person details into the database.
    media.save(person);
    ingest.loaded();
    repository.save(ingest);
  }
}
