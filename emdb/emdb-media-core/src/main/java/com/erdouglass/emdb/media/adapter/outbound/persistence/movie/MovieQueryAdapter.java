package com.erdouglass.emdb.media.adapter.outbound.persistence.movie;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.adapter.inbound.graphql.movie.MovieView;
import com.erdouglass.emdb.media.application.port.outbound.movie.MovieQueryRepository;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

@ApplicationScoped
class MovieQueryAdapter implements MovieQueryRepository {

  @Override
  public Optional<MovieView> findById(MoviePublicId id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<MovieView> findByTmdbId(TmdbId tmdbId) {
    throw new UnsupportedOperationException();
  }
}
