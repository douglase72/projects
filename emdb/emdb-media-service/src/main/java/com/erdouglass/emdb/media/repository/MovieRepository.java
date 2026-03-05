package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.Movie;

/// Data access layer for `Movie` entities.
///
/// Extends `CrudRepository` to provide standard create, read, update, 
/// and delete operations.
@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {
  
  /// Retrieves a movie by its corresponding external TMDB identifier.
  ///
  /// @param tmdbId the external TMDB identifier
  /// @return an [Optional] containing the movie if found, or empty if it does not exist
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Movie> findByTmdbId(Integer tmdbId);
  
}
