package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.outbound.IngestRepository;
import com.erdouglass.emdb.ingest.application.port.outbound.MovieSource;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.media.movie.SaveMovieUseCase;

@ApplicationScoped
class MovieIngestService {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  IngestRepository repository;
  
  @Inject
  SaveMovieUseCase saveUseCase;
  
  @Inject
  MovieSource source;

  public void execute(Ingest ingest) {
    
    // Extract the movie details from TMDB.
    var movie = source.extract(ingest.tmdbId());
    ingest.extracted();
    repository.save(ingest);
    
    // Load the movie details into the database.
    saveUseCase.save(mapper.toSaveMovieCommand(movie));
    ingest.loaded();
    repository.save(ingest);
  }
}
