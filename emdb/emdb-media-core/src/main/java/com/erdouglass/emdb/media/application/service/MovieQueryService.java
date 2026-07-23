package com.erdouglass.emdb.media.application.service;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.application.port.inbound.FindMovieUseCase;
import com.erdouglass.emdb.media.application.port.outbound.MovieRepository;
import com.erdouglass.emdb.media.domain.movie.Movie;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

@ApplicationScoped
class MovieQueryService implements FindMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieQueryService.class);

  @Inject
  MovieRepository repository;
  
  @Override
  public Optional<Movie> findById(MoviePublicId id) {
    var movie = repository.findByPublicId(id);
    movie.ifPresent(m -> LOGGER.infof("Found: %s", m));
    return movie;
  }
}
