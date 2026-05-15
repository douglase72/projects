package com.erdouglass.emdb.media.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.jboss.logging.Logger;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MovieRepository respoistory;
  
  @Transactional
  public Movie save(@NotNull @Valid final Movie movie) {
    var savedMovie = respoistory.insert(movie);
    LOGGER.infof("Saved: %s", savedMovie);
    return savedMovie;
  }
}
