package com.erdouglass.emdb.media.domain.series;

import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

/// Jakarta Data repository for [Series] persistence. Extends the standard CRUD
/// operations with lookup by external TMDB identifier.
@Repository
interface SeriesRepository extends CrudRepository<Series, Long> {

  /// Retrieves a series by its corresponding external TMDB identifier.
  ///
  /// @param tmdbId the external TMDB identifier
  /// @return an [Optional] containing the series if found, or empty if it does not exist
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Series> findByTmdbId(Integer tmdbId);
}
