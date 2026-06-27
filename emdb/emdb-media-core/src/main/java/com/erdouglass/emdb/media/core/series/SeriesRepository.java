package com.erdouglass.emdb.media.core.series;

import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository(dataStore = "media")
interface SeriesRepository extends CrudRepository<Series, Long> {

  /// Retrieves a series by its corresponding external TMDB identifier.
  ///
  /// @param tmdbId the external TMDB identifier
  /// @return an [Optional] containing the series if found, or empty if it does
  ///         not exist
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Series> findByTmdbId(Integer tmdbId);  
}
