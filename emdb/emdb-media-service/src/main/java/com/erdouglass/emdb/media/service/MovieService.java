package com.erdouglass.emdb.media.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.proto.v1.SaveMovieRequest;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MovieMapper mapper;
  
  public Movie save(SaveMovieRequest request) {
    var savedMovie = mapper.toMovie(request);
    LOGGER.infof("Saved: %s", savedMovie);
    return savedMovie;
  }

}
