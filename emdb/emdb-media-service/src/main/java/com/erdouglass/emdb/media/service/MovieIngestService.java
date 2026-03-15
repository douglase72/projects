package com.erdouglass.emdb.media.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.scraper.service.TmdbMovieScraper;

@ApplicationScoped
public class MovieIngestService {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  TmdbMovieScraper scraper;
  
  @Inject
  MovieService service;
  
  public void ingest(@NotNull @Positive Integer tmdbId) {
    var command = service.findByTmdbId(tmdbId)
        .map(m -> scraper.extract(mapper.toSaveMovie(m)))
        .orElseGet(() -> scraper.extract(tmdbId));
    
    try {
      service.save(command);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }    
  }

}
