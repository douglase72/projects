package com.erdouglass.emdb.media.service;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.logging.LogDuration;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.repository.MovieRepository;

@ApplicationScoped
public class MovieService {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;
  
  @Transactional
  @LogDuration("Saved:")
  public Movie save(@NotNull @Valid SaveMovie command) {
    Movie savedMovie;
    var existingMovie = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existingMovie == null) {
      savedMovie = repository.insert(mapper.toMovie(command));
    } else {
      mapper.merge(command, existingMovie);
      savedMovie = repository.update(existingMovie);
    }
    return savedMovie;
  }
  
  @Transactional
  @LogDuration("Found:")
  public Optional<Movie> findById(@NotNull @Positive Long id, String append) {
    var movie = repository.findById(id);
    return movie;
  }

}
