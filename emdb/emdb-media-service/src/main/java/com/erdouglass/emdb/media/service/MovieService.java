package com.erdouglass.emdb.media.service;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieUpdateCommand;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.repository.MovieRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/// Implements the business logic for all `Movie`-related operations.
///
/// This service acts as the intermediary between the `MovieResource` (web layer)
/// and the `MovieRepository` (data access layer).
///
/// Its primary responsibilities include:
///
/// * Managing transactions (`@Transactional`) for all database operations.
/// * Fetching entities from the repository or throwing a `ResourceNotFoundException`
///     if an entity is not found.
/// * Containing the "patch" logic for applying partial updates from a
///     `MovieUpdateCommand` to an existing `Movie` entity.
/// * Coordinating the `create` and `delete` operations.
/// * Logging all major operations.
///
/// @see com.erdouglass.emdb.media.repository.MovieRepository
/// @see com.erdouglass.emdb.media.controller.MovieResource
/// @see com.erdouglass.emdb.media.entity.Movie
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
	
	@Transactional
	public Movie update(@NotNull @Positive Long id, @NotNull @Valid MovieUpdateCommand command) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    command.title().ifPresent(movie::name);
    command.releaseDate().ifPresent(movie::releaseDate);
    command.score().ifPresent(movie::score);
    command.status().ifPresent(movie::status);
    command.runtime().ifPresent(movie::runtime);
    command.budget().ifPresent(movie::budget);
    command.revenue().ifPresent(movie::revenue);
    command.homepage().ifPresent(movie::homepage);
    command.originalLanguage().ifPresent(movie::originalLanguage);
    command.backdrop().ifPresent(movie::backdrop);
    command.poster().ifPresent(movie::poster);
    command.tagline().ifPresent(movie::tagline);
    command.overview().ifPresent(movie::overview);
    LOGGER.infof("Updated: %s", movie);
		return movie;
	}
	
  @Transactional
  public void delete(@NotNull @Positive Long id) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    repository.deleteById(id);
    LOGGER.infof("Deleted: %s", movie);
  }
	
}
