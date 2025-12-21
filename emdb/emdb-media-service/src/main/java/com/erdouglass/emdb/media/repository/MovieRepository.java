package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.Movie;

/// The Jakarta Data repository interface for the `Movie` entity.
///
/// This interface defines the contract for all database-level CRUD operations.
/// By annotating this interface with `@Repository`, Quarkus and the underlying
/// Jakarta Data provider (e.g., Hibernate) will automatically generate the
/// implementation for these methods at build time.
@Repository
public interface MovieRepository {

  /// Persists a new {@link Movie} entity in the database.
  ///
  /// @param movie The new, transient {@link Movie} object to save.
  /// @return The persisted {@link Movie}, now managed and containing a generated ID.
  @Insert
  Movie insert(Movie movie);
  
  /// Finds a {@link Movie} by its surrogate primary key (ID).
  ///
  /// @param id The primary key of the {@link Movie} to find.
  /// @return An {@link Optional} containing the found {@link Movie}, or
  ///         {@link Optional#empty()} if not found.
  @Find
  Optional<Movie> findById(Long id);
  
}
