package com.erdouglass.emdb.media.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.repository.MovieRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MovieRepository repository;
  
  @Transactional
  public Movie create(@NotNull @Valid Movie movie) {
    var newMovie = repository.insert(movie);
    LOGGER.infof("Created: %s", newMovie);   
    return newMovie;
  }
  
  @Transactional
  public Movie findById(@NotNull @Positive Long id) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    LOGGER.infof("Found: %s", movie);
    return movie;
  }

}
