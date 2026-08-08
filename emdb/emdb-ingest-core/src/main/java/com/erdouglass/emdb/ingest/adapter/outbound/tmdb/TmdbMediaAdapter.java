package com.erdouglass.emdb.ingest.adapter.outbound.tmdb;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.outbound.Media;
import com.erdouglass.emdb.ingest.application.port.outbound.MediaSource;
import com.erdouglass.emdb.ingest.domain.model.IngestType;
import com.erdouglass.emdb.media.TmdbId;

/// Anti-corruption layer over TMDB API.
@ApplicationScoped
class TmdbMediaAdapter implements MediaSource {
  
  @Inject
  TmdbMovieAdapter movieAdapter;

  @Override
  public Media extract(TmdbId tmdbId, IngestType type) {
    return switch (type) {
      case MOVIE -> movieAdapter.extract(tmdbId.value());
      default -> throw new IllegalArgumentException();
    };
  }
}
