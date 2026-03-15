package com.erdouglass.emdb.media.service;

import java.util.Objects;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.dto.SaveResult.Status;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.logging.LogDuration;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.repository.MovieRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class MovieService {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;
  
  @Transactional
  @LogDuration("Saved:")
  public SaveResult<Movie> save(@NotNull @Valid SaveMovie command) {
    var existingMovie = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existingMovie == null) {
      var insertedMovie = repository.insert(mapper.toMovie(command));
      return SaveResult.of(Status.CREATED, insertedMovie);
    } else if (!isEqual(command, existingMovie)) {
      mapper.merge(command, existingMovie);
      var updatedMovie = repository.update(existingMovie);
      return SaveResult.of(Status.UPDATED, updatedMovie);
    }
    return SaveResult.of(Status.UNCHANGED, existingMovie);
  }
  
  @Transactional
  @LogDuration("Found:")
  public Optional<Movie> findById(@NotNull @Positive Long id, String append) {
    var movie = repository.findById(id);
    return movie;
  }
  
  @Transactional
  @LogDuration("Found:")
  public Optional<Movie> findByTmdbId(@NotNull @Positive Integer id) {
    return repository.findByTmdbId(id);
  }  
  
  @Transactional
  @LogDuration("Updated:")
  public Movie update(Long id, UpdateMovie command) {
    var existingMovie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    mapper.merge(command, existingMovie);
    return repository.update(existingMovie);
  }
  
  @Transactional
  @LogDuration(value = "Deleted:", subject = "movie")
  public void delete(Long id) {
    repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    repository.deleteById(id);
  }
  
  private boolean isEqual(SaveMovie command, Movie movie) {
    if (command == null || movie == null) return false;
    return Objects.equals(command.tmdbId(), movie.getTmdbId())
        && Objects.equals(command.title(), movie.getTitle())
        && Objects.equals(command.releaseDate(), movie.getReleaseDate())
        && Objects.equals(command.score(), movie.getScore())
        && Objects.equals(command.status(), movie.getStatus())
        && Objects.equals(command.runtime(), movie.getRuntime())
        && Objects.equals(command.budget(), movie.getBudget())
        && Objects.equals(command.backdrop(), movie.getBackdrop())
        && Objects.equals(command.poster(), movie.getPoster())
        && Objects.equals(command.homepage(), movie.getHomepage())
        && Objects.equals(command.originalLanguage(), movie.getOriginalLanguage())
        && Objects.equals(command.overview(), movie.getOverview());
  }

}
