package com.erdouglass.emdb.media.domain.series;

import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository(dataStore = "media")
public interface SeriesRepository extends CrudRepository<Series, Long> {

  /// Retrieves a series by its corresponding external identifier.
  ///
  /// @param externalId the external identifier
  /// @return an [Optional] containing the series if found, or empty if it does not exist
  @Query("WHERE externalId = :externalId")
  Optional<Series> findByExternalId(Long externalId);
}
