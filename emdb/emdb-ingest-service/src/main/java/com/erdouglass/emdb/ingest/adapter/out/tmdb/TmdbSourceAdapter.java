package com.erdouglass.emdb.ingest.adapter.out.tmdb;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.IngestMediaCommand.IngestType;
import com.erdouglass.emdb.ingest.application.port.out.Media;
import com.erdouglass.emdb.ingest.application.port.out.MediaSource;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

@ApplicationScoped
class TmdbSourceAdapter implements MediaSource {
  
  @Inject
  TmdbMovieScraper movies;
  
  @Inject
  TmdbPersonScraper people;

  @Override
  public Media extract(TmdbId tmdbId, IngestType type) {
    return switch (type) {
      case MOVIE -> movies.scrape(tmdbId);
      case PERSON -> people.scrape(tmdbId);
      case SERIES -> throw new UnsupportedOperationException();
    };
  }
}
