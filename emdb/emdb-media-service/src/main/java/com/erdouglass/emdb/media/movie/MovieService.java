package com.erdouglass.emdb.media.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.jboss.logging.Logger;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  public Movie save(@NotNull @Valid final Movie movie) {
    var m = new Movie();
    m.setId(1L);
    m.setTitle(movie.getTitle());
    m.setReleaseDate(movie.getReleaseDate());
    LOGGER.infof("Saved: %s", m);
    return m;
  }
}
