package com.erdouglass.emdb.media.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.mapper.MovieMapper;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MovieMapper mapper;
  
  public Movie save(@NotNull @Valid SaveMovie command) {
    var savedMovie = mapper.toMovie(command);
    LOGGER.infof("Saved: %s", savedMovie);
    return savedMovie;
  }

}
