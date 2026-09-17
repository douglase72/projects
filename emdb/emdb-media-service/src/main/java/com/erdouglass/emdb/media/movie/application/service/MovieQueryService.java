package com.erdouglass.emdb.media.movie.application.service;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.movie.application.port.in.FindMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.MovieView;
import com.erdouglass.emdb.media.movie.application.port.out.MovieQueryRepository;

@ApplicationScoped
class MovieQueryService implements FindMovieUseCase {
  
  @Inject
  MovieQueryRepository movies;

  @Override
  public Optional<MovieView> findById(PublicId id) {
    return movies.findById(id);
  }
}
