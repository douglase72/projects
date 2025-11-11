package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import com.erdouglass.emdb.media.entity.Movie;

import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

/// The Jakarta Data repository interface for the `Movie` entity.
///
/// This interface defines the contract for all database-level CRUD operations.
/// By annotating this interface with `@Repository`, Quarkus and the underlying
/// Jakarta Data provider (e.g., Hibernate) will automatically generate the
/// implementation for these methods at build time.
///
/// It uses standard Jakarta Data lifecycle annotations like `@Insert`, `@Find`,
/// `@Update`, and `@Delete` to declare the intended operation.
///
/// @see com.erdouglass.emdb.media.entity.Movie
/// @see com.erdouglass.emdb.media.service.MovieService
@Repository
public interface MovieRepository {
	
	/// Persists a new {@link Movie} entity in the database.
	///
	/// @param movie The new, transient movie object to save.
	/// @return The persisted movie, now managed and containing a generated ID.
	@Insert
	Movie insert(Movie movie);
	
	/// Finds a {@link Movie} by its surrogate primary key (ID).
	///
	/// @param id The primary key of the movie to find.
	/// @return An {@link Optional} containing the found movie, or
	///         {@link Optional#empty()} if not found.
	@Find
	Optional<Movie> findById(Long id);
	
	/// Finds a {@link Movie} by its natural (business) key, which is the
	/// combination of the data source and the external ID.
	///
	/// @param source The data source (e.g., "tmdb").
	/// @param externalId The external identifier (must be a {@code Long}).
	/// @return An {@link Optional} containing the found movie, or
	///         {@link Optional#empty()} if not found.
	@Query("WHERE source = :source AND externalId = :externalId")
	Optional<Movie> findByNaturalId(String source, Long externalId);
	
	/// Updates an existing {@link Movie} in the database.
	///
	/// @param movie The movie entity (typically detached) with updated fields.
	/// @return The updated, managed movie entity.
	@Update
	Movie update(Movie movie);
	
	/// Deletes a {@link Movie} from the database by its surrogate primary key.
	///
	/// @param id The primary key of the movie to delete.
  @Delete
  void deleteById(Long id);

}
