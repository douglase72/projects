package com.erdouglass.emdb.ingest.adapter.outbound.tmdb;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.outbound.Media;
import com.erdouglass.emdb.ingest.application.port.outbound.MediaSource;
import com.erdouglass.emdb.ingest.domain.model.IngestSource;

/// Anti-corruption layer over TMDB API.
@ApplicationScoped
class TmdbMediaAdapter implements MediaSource {
  
  @Inject
  TmdbMovieAdapter movieAdapter;

  @Override
  public Media extract(IngestSource source) {
    return switch (source.type()) {
      case MOVIE -> movieAdapter.extract(source.source().id());
      default -> throw new IllegalArgumentException();
    };
  }
}
