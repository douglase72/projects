package com.erdouglass.emdb.media.movie.adapter.out;

import java.util.Optional;

import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

/// Jakarta Data repository for the write side of the movie table.
///
/// Deals in entities and raw keys; translating between those and the aggregate
/// is [MovieCommandAdapter]'s job. Reads here exist to load an aggregate for
/// modification, not to serve queries — client reads go through
/// [JakartaDataMovieQueryRepository], which projects instead of loading.
///
/// The `Long` ids in these signatures are the numeric primary key, not the
/// prefixed catalogue id, and not the surrogate UUID.
@Repository(dataStore = "media")
interface JakartaDataMovieCommandRepository {

  /// Inserts a new row and returns it with the database-assigned key and
  /// version populated.
  ///
  /// @param entity the row to insert; its id must be `null` so the sequence
  ///        supplies one
  /// @return the inserted row, now carrying its key
  @Insert
  MovieEntity insert(MovieEntity entity);
  
  /// Updates an existing row, checking the optimistic-locking version.
  ///
  /// @param entity the row to write, carrying the version that was read
  /// @return the updated row with the incremented version
  /// @throws OptimisticLockingFailureException if the stored version has moved
  ///         on since the entity was read
  @Update
  MovieEntity update(MovieEntity entity);
  
  /// Deletes the row with the given key.
  ///
  /// @param id the numeric primary key
  @Delete
  void deleteById(Long id);
  
  /// Loads a row by primary key.
  ///
  /// @param id the numeric primary key
  /// @return the row, or empty if none carries that key
  @Find
  Optional<MovieEntity> findById(Long id);
  
  /// Loads a row by its TMDB id, the lookup that makes ingestion an upsert.
  ///
  /// Backed by a unique constraint, so at most one row can match.
  ///
  /// @param tmdbId the natural id from the upstream catalogue
  /// @return the row, or empty if the title has not been ingested yet
  @Find
  Optional<MovieEntity> findByTmdbId(Integer tmdbId);
}
