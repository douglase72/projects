package com.erdouglass.emdb.media.movie.application.service;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.movie.application.port.in.FindMovieCreditsUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.FindMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.MovieCreditView;
import com.erdouglass.emdb.media.movie.application.port.in.MovieView;
import com.erdouglass.emdb.media.movie.application.port.out.MovieQueryRepository;
import com.erdouglass.emdb.media.movie.domain.model.MoviePublicId;

@ApplicationScoped
class MovieQueryService implements FindMovieUseCase, FindMovieCreditsUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieQueryService.class);

  @Inject
  MovieQueryRepository query;
  
  @Override
  public Optional<MovieView> findById(MoviePublicId id) {
    var movie = query.findById(id);
    movie.ifPresent(m -> LOGGER.infof("Found: %s", m));
    return movie;
  }

  @Override
  public List<MovieCreditView> findByMovieId(MoviePublicId movieId) {
    return query.findCreditsByMovieId(movieId);
  }
}
