package com.erdouglass.emdb.media.service;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.repository.MovieRepository;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository respository;
  
  @Transactional
  public Movie save(@NotNull @Valid SaveMovie command) {
    var savedMovie = respository.save(mapper.toMovie(command));
    LOGGER.infof("Saved: %s", savedMovie);
    return savedMovie;
  }
  
  @Transactional
  public Optional<Movie> findById(@NotNull @Positive Long id, String append) {
    var movie = respository.findById(id);
    movie.ifPresent(m -> LOGGER.infof("Found: %s", m));
    return movie;
  }

}
