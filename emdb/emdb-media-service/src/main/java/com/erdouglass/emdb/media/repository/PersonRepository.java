package com.erdouglass.emdb.media.repository;

import java.util.List;
import java.util.Optional;

import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.api.query.PersonView;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.service.PersonCrudService;

/// Repository for [Person] entity persistence.
///
/// Provides standard CRUD operations via [CrudRepository] and custom
/// queries for TMDB ID lookups and paginated list views.
@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
  
  /// Returns a paginated list of [PersonView] projections sorted by name.
  @Query("""
      SELECT NEW com.erdouglass.emdb.media.api.query.PersonView(
          p.id, p.name, p.birthDate, p.gender, CAST(p.profile AS STRING))
      FROM Person p
      ORDER BY p.name ASC, p.id ASC
      """)
  Page<PersonView> find(PageRequest pageRequest);

  /// Finds a person by their unique TMDB ID.
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Person> findByTmdbId(Integer tmdbId);
  
  /// Finds all people whose TMDB ID is in the given list.
  ///
  /// Used by [PersonCrudService#saveAll] to pre-fetch existing entities
  /// and minimize database round trips during batch saves.  
  @Query("WHERE tmdbId IN :tmdbIds")
  List<Person> findByTmdbIdIn(List<Integer> tmdbIds);
}
