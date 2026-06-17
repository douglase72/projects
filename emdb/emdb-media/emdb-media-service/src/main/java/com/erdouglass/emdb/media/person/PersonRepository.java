package com.erdouglass.emdb.media.person;

import java.util.List;
import java.util.Optional;

import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

/// Jakarta Data repository for [Person] persistence. Extends the standard CRUD
/// operations with lookup by external TMDB identifier.
@Repository
interface PersonRepository extends CrudRepository<Person, Long> {
  
  @Find
  Page<Person> findAll(PageRequest pageRequest, Sort<Person> sort);

  /// Retrieves a person by their corresponding external TMDB identifier.
  ///
  /// @param tmdbId the external TMDB identifier
  /// @return an [Optional] containing the person if found, or empty if it does
  ///         not exist
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Person> findByTmdbId(Integer tmdbId);

  /// Retrieves all people whose TMDB identifiers are in the given collection,
  /// resolving a batch of credits in a single query.
  ///
  /// @param tmdbIds the external TMDB identifiers to look up
  /// @return the matching people, in no guaranteed order; empty if none match
  @Query("WHERE tmdbId IN :tmdbIds")
  List<Person> findByTmdbIdIn(List<Integer> tmdbIds);
}
