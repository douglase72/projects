package com.erdouglass.emdb.media.movie.application.service;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.movie.application.port.in.FindMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieQueryRepository;
import com.erdouglass.emdb.media.movie.application.port.out.MovieView;

@ApplicationScoped
class MovieQueryService implements FindMovieUseCase {
  
  @Inject
  MovieQueryRepository movies;

  @Override
  public Optional<MovieView> findById(PublicId id) {
    return movies.findById(id);
  }
}
