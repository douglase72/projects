package com.erdouglass.emdb.media.domain.movie;

import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository(dataStore = "media")
public interface MovieRepository extends CrudRepository<Movie, Long> {

  /// Retrieves a movie by its corresponding external identifier.
  ///
  /// @param externalId the external identifier
  /// @return an [Optional] containing the movie if found, or empty if it does not exist
  @Query("WHERE externalId = :externalId")
  Optional<Movie> findByExternalId(Long externalId);
}
