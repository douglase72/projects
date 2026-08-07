package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.adapter.inbound.graphql.movie.MovieView;
import com.erdouglass.emdb.media.application.port.inbound.movie.FindMovieUseCase;
import com.erdouglass.emdb.media.application.port.outbound.movie.MovieQueryRepository;
import com.erdouglass.emdb.media.domain.exception.MovieNotFoundException;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

@ApplicationScoped
class MovieQueryService implements FindMovieUseCase {
  
  @Inject
  MovieQueryRepository query;

  @Override
  public MovieView findById(MoviePublicId id) {
    return query.findById(id).orElseThrow(() -> new MovieNotFoundException(id.value()));
  }
}
