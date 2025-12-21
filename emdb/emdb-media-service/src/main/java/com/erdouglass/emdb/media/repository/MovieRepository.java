package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

import com.erdouglass.emdb.media.entity.Movie;

/// A Jakarta Data repository interface for performing CRUD operations on the 
/// {@link Movie} domain entity.
///
/// This interface defines the contract for all Create, Read, Update, and 
/// Delete (CRUD) operations. The underlying Jakarta Data provider will 
/// automatically generate the implementation for these methods at build time.
@Repository
public interface MovieRepository {

  /// Inserts a {@link Movie} into the database. 
  ///
  /// If a movie of this type with the same unique identifier already exists in the 
  /// database then this method raises a {@link EntityExistsException}. 
  ///
  /// @param movie the movie to insert. Must not be {@code null}.
  /// @return the inserted movie with the generated ID.
  @Insert
  Movie insert(Movie movie);
  
  /// Finds a {@link Movie} by its surrogate primary key (ID).
  ///
  /// @param id The primary key of the {@link Movie} to find, must not be {@code null}.
  /// @return An {@link Optional} containing the found {@link Movie}, or
  ///         {@link Optional#empty()} if not found.
  @Find
  Optional<Movie> findById(Long id);
  
  /// Modifies a {@link Movie} that already exists in the database.
  ///
  /// For an update to be made, a matching entity with the same unique identifier
  /// must be present in the database. 
  ///
  /// @param movie the movie to update. Must not be {@code null}.
  /// @return an updated movie instance including all automatically generated 
  ///         values, updated versions, and incremented values which changed as a 
  ///         result of the update.
  @Update
  Movie update(Movie movie);
  
  /// Deletes a {@link Movie} from the database by its surrogate primary key.
  ///
  /// @param id the primary key of the movie to delete.
  @Delete
  void deleteById(Long id);
  
}
