package com.erdouglass.emdb.media.movie.application.service;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.movie.application.port.in.FindMovieCreditsUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.FindMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.MovieCreditView;
import com.erdouglass.emdb.media.movie.application.port.in.MovieView;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCreditQueryRepository;
import com.erdouglass.emdb.media.movie.application.port.out.MovieQueryRepository;

@ApplicationScoped
class MovieQueryService implements FindMovieUseCase, FindMovieCreditsUseCase {
  
  @Inject
  MovieCreditQueryRepository credits;
  
  @Inject
  MovieQueryRepository movies;

  @Override
  public Optional<MovieView> findById(PublicId id) {
    return movies.findById(id);
  }

  @Override
  public List<MovieCreditView> findByMovieId(PublicId id) {
    return credits.findByMovieId(id);
  }
}
