package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.Series;

/// Data access layer for [Series] entities.
///
/// Extends `CrudRepository` to provide standard create, read, update, 
/// and delete operations.
@Repository
public interface SeriesRepository extends CrudRepository<Series, Long> {

  /// Retrieves a series by its corresponding external TMDB identifier.
  ///
  /// @param tmdbId the external TMDB identifier
  /// @return an [Optional] containing the series if found, or empty if it does not exist
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Series> findByTmdbId(Integer tmdbId);
  
}
