package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.out.IngestRepository;
import com.erdouglass.emdb.ingest.application.port.out.Media;
import com.erdouglass.emdb.ingest.application.port.out.MovieSource;
import com.erdouglass.emdb.ingest.domain.model.Ingest;

@ApplicationScoped
class MovieIngestService {
  
  @Inject
  Media media;
  
  @Inject
  IngestRepository repository;
  
  @Inject
  MovieSource source;

  public void execute(Ingest ingest) {
    
    // Extract the movie details from TMDB.
    var movie = source.extract(ingest.tmdbId());
    ingest.extracted();
    repository.save(ingest);
    
    // Load the movie details into the database.
    media.save(movie);
    ingest.loaded();
    repository.save(ingest);
  }
}
