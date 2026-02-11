package com.erdouglass.emdb.media.repository;

import java.util.List;
import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
  
  /// Finds a person by their natural business key (TMDB ID).
  ///
  /// @param tmdbId The unique identifier from The Movie Database.
  /// @return An `Optional` containing the person if found.
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Person> findByTmdbId(Integer tmdbId); 
  
  @Query("WHERE id IN :ids")
  List<Person> findByIdIn(List<Long> ids);
  
  /// Finds multiple people by a list of natural business keys.
  /// This is used for efficient batch processing to minimize database round-trips.
  ///
  /// @param tmdbIds A list of TMDB IDs to search for.
  /// @return A list of person entities matching the provided IDs.
  @Query("WHERE tmdbId IN :tmdbIds")
  List<Person> findByTmdbIdIn(List<Integer> tmdbIds);  
  
}