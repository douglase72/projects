package com.erdouglass.emdb.media.service;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieUpdateCommand;
import com.erdouglass.emdb.common.configuration.Configuration;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.repository.MovieRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

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
	
	/// Persists a new, validated {@link Movie} entity in the database.
	///
	/// @param movie The movie object to create. Must be valid and not null.
	/// @return The persisted {@code Movie} entity, now containing its generated ID.
	@Transactional
	public Movie create(@NotNull @Valid Movie movie) {
		var newMovie = repository.insert(movie);
		LOGGER.infof("Created: %s", newMovie);
		return newMovie;
	}
	
	/// Finds a {@link Movie} by its surrogate primary key (ID).
	///
	/// @param id The primary key of the movie to find. Must be positive and not null.
	/// @return The found {@code Movie} entity.
	/// @throws ResourceNotFoundException if no movie is found with the given ID.
	@Transactional
	public Movie findById(@NotNull @Positive Long id) {
		var movie = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
  	LOGGER.infof("Found: %s", movie);
  	return movie;		
	}
	
	/// Finds a {@link Movie} by its natural business key (source + externalId).
	///
	/// @param source The data source (e.g., "tmdb"). Must not be blank.
	/// @param externalId The external identifier. Must be positive and not null.
	/// @return The found {@code Movie} entity.
	/// @throws ResourceNotFoundException if no movie is found with the given key.
	@Transactional
	public Movie findByNaturalId(
			@NotBlank @Size(max = Configuration.SOURCE_MAX_LENGTH) String source,
			@NotNull @Positive Long externalId) {
		var movie = repository.findByNaturalId(source, externalId)
				.orElseThrow(() -> {
					var msg = String.format("No movie found with id: %s, %d", source, externalId);
					throw new ResourceNotFoundException(msg);
				});
  	LOGGER.infof("Found: %s", movie);
  	return movie;		
	}
	
	/// Applies a partial update to an existing {@link Movie} using a command object.
	///
	/// This method fetches the existing movie and then applies only the non-empty
	/// {@code Optional} fields from the {@code MovieUpdateCommand} to it.
	///
	/// @param id The ID of the movie to update. Must be positive and not null.
	/// @param command The command object containing the partial updates.
	/// @return The updated, persisted {@code Movie} entity.
	/// @throws ResourceNotFoundException if no movie is found with the given ID.
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
    var updatedMovie = repository.update(movie);
    LOGGER.infof("Updated: %s", updatedMovie);
		return updatedMovie;
	}
	
	/// Deletes a {@link Movie} by its surrogate primary key (ID).
	///
	/// It first fetches the movie to ensure it exists before attempting deletion.
	///
	/// @param id The ID of the movie to delete. Must be positive and not null.
	/// @throws ResourceNotFoundException if the movie does not exist.
  @Transactional
  public void delete(@NotNull @Positive Long id) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    repository.deleteById(id);
    LOGGER.infof("Deleted: %s", movie);
  }
	
}
