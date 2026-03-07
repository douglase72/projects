package com.erdouglass.emdb.media.service;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.UpdateMovie;
import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.dto.SaveResult.Status;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.logging.LogDuration;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.repository.MovieRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

/// Service layer responsible for business logic related to movie entities.
///
/// Handles database transactions, interacts with the external TMDB API
/// via [TmdbMovieScraper], maps entities to DTOs, and logs operation execution
/// times for performance monitoring.
@ApplicationScoped
public class MovieService {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieRepository repository;
  
  /// Persists a new movie or updates an existing one based on its external TMDB ID.
  ///
  /// @param command the payload containing data for the movie to be saved
  /// @return a [MovieStatus] wrapper containing the operation outcome and the saved entity
  @Transactional
  @LogDuration("Saved:")
  public SaveResult<Movie> save(SaveMovie command) {
    Movie savedMovie;
    Status status;
    var existingMovie = repository.findByTmdbId(command.tmdbId());
    if (existingMovie.isPresent()) {
      var movieToUpdate = existingMovie.get();
      mapper.merge(command, movieToUpdate);
      savedMovie = repository.update(movieToUpdate);
      status = Status.UPDATED;
    } else {
      savedMovie = repository.insert(mapper.toMovie(command));
      status = Status.CREATED;
    }
    return SaveResult.of(status, savedMovie);
  }
  
  @Transactional
  @LogDuration("Found:")
  public Optional<Movie> findById(Long id) {
    return repository.findById(id);
  }
  
  @Transactional
  @LogDuration("Found:")
  public Optional<Movie> findByTmdbId(Integer id) {
    return repository.findByTmdbId(id);
  }
  
  /// Updates an existing movie with new information while retaining its core IDs.
  ///
  /// @param id      the internal ID of the movie being updated
  /// @param command the payload containing the new movie data
  /// @return a mapped [MovieDto] representing the updated entity  
  @Transactional
  @LogDuration("Updated:")
  public Movie update(Long id, UpdateMovie command) {
    var existingMovie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    mapper.merge(command, existingMovie);
    return repository.update(existingMovie);
  }
  
  /// Deletes an existing movie by its ID.
  ///
  /// @param id the internal ID of the movie to remove
  @Transactional
  @LogDuration("Deleted")
  public void deleteById(Long id) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    repository.delete(movie);
  }  
  
}
