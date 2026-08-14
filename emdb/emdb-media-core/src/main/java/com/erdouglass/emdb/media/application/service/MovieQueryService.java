package com.erdouglass.emdb.media.application.service;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.adapter.inbound.movie.MovieView;
import com.erdouglass.emdb.media.application.port.inbound.movie.FindMovieUseCase;
import com.erdouglass.emdb.media.application.port.outbound.movie.MovieQueryRepository;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

@ApplicationScoped
class MovieQueryService implements FindMovieUseCase {
  
  @Inject
  MovieQueryRepository query;

  @Override
  public Optional<MovieView> findById(MoviePublicId id) {
    return query.findById(id);
  }
}
