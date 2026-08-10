package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.adapter.inbound.movie.MovieView;
import com.erdouglass.emdb.media.application.port.inbound.movie.FindMovieUseCase;
import com.erdouglass.emdb.media.application.port.outbound.movie.MovieQueryRepository;
import com.erdouglass.emdb.media.domain.exception.MovieNotFoundException;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

@ApplicationScoped
class MovieQueryService implements FindMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieQueryService.class);
  
  @Inject
  MovieQueryRepository query;

  @Override
  public MovieView findById(MoviePublicId id) {
    var movie = query.findById(id).orElseThrow(() -> new MovieNotFoundException(id.value()));
    LOGGER.infof("Found: %s", movie);
    return movie;
  }
}
