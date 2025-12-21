package com.erdouglass.emdb.media.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.request.MovieUpdateRequest;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.repository.MovieRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

/// Service class encapsulating business logic for managing {@link Movie} entities.
///
/// This service acts as the transactional boundary for operations and orchestrates
/// the interaction between the controller layer and the persistence layer. It handles
/// validation, logging, and exception mapping for missing resources.
@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  @Inject
  MovieRepository repository;
  
  /// Persists a new {@link Movie} to the database.
  ///
  /// This method operates within a transaction. It delegates the insertion to the
  /// repository and logs the creation event.
  ///
  /// @param movie the {@link Movie} entity to create. Must not be {@code null}
  ///        and must be valid.
  /// @return the persisted movie instance with its generated ID.
  @Transactional
  public Movie create(@NotNull @Valid Movie movie) {
    var newMovie = repository.insert(movie);
    LOGGER.infof("Created: %s", newMovie);   
    return newMovie;
  }
  
  /// Retrieves a specific {@link Movie} by its unique identifier.
  ///
  /// @param id the unique identifier of the movie. Must be a positive number.
  /// @return the found {@link Movie} entity.
  /// @throws ResourceNotFoundException if no movie exists with the provided {@code id}.
  @Transactional
  public Movie findById(@NotNull @Positive Long id) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    LOGGER.infof("Found: %s", movie);
    return movie;
  }
  
  /// Performs a partial update on an existing {@link Movie}.
  ///
  /// This method first retrieves the existing entity. It then iterates through
  /// the fields present in the {@link MovieUpdateRequest} and applies them to
  /// the managed entity only if the value is present (not null/empty).
  ///
  /// @param id the unique identifier of the movie to update.
  /// @param request the DTO containing the fields to update. Must not be {@code null}.
  /// @return the updated {@link Movie} entity reflected in the database.
  /// @throws ResourceNotFoundException if the movie to update does not exist.
  @Transactional
  public Movie update(@NotNull @Positive Long id, @NotNull @Valid MovieUpdateRequest request) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    request.title().ifPresent(movie::name);
    request.releaseDate().ifPresent(movie::releaseDate);
    request.score().ifPresent(movie::score);
    request.status().ifPresent(movie::status);
    request.runtime().ifPresent(movie::runtime);
    request.budget().ifPresent(movie::budget);
    request.revenue().ifPresent(movie::revenue);
    request.homepage().ifPresent(movie::homepage);
    request.originalLanguage().ifPresent(movie::originalLanguage);
    request.backdrop().ifPresent(movie::backdrop);
    request.poster().ifPresent(movie::poster);
    request.tagline().ifPresent(movie::tagline);
    request.overview().ifPresent(movie::overview);
    var updatedMovie = repository.update(movie);
    LOGGER.infof("Updated: %s", updatedMovie);
    return updatedMovie;
  }
  
  /// Deletes a {@link Movie} from the system.
  ///
  /// This method verifies the existence of the movie before attempting deletion.
  /// If the movie is found, it is removed from the repository.
  ///
  /// @param id the unique identifier of the movie to delete.
  /// @throws ResourceNotFoundException if the movie to delete does not exist.
  @Transactional
  public void delete(@NotNull @Positive Long id) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    repository.deleteById(id);
    LOGGER.infof("Deleted: %s", movie);
  }

}
