package com.erdouglass.emdb.media.domain.person;

import java.util.List;
import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

/// Jakarta Data repository for [Person] persistence. Extends the standard CRUD
/// operations with lookup by external TMDB identifier.
@Repository
interface PersonRepository extends CrudRepository<Person, Long> {

  /// Retrieves a person by their corresponding external TMDB identifier.
  ///
  /// @param tmdbId the external TMDB identifier
  /// @return an [Optional] containing the person if found, or empty if it does not exist
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Person> findByTmdbId(Integer tmdbId);
  
  @Query("WHERE tmdbId IN :tmdbIds")
  List<Person> findByTmdbIdIn(List<Integer> tmdbIds); 
}
