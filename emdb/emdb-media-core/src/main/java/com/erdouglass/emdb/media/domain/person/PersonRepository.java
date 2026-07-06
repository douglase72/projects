package com.erdouglass.emdb.media.domain.person;

import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository(dataStore = "media")
public interface PersonRepository extends CrudRepository<Person, Long> {

  /// Retrieves a person by its corresponding external identifier.
  ///
  /// @param externalId the external identifier
  /// @return an [Optional] containing the person if found, or empty if it does not exist
  @Query("WHERE externalId = :externalId")
  Optional<Person> findByExternalId(Long externalId);
}
