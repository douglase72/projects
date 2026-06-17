package com.erdouglass.emdb.media.movie;

import java.util.Optional;

import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

/// Jakarta Data repository for [Movie] persistence. Extends the standard CRUD
/// operations with lookup by external TMDB identifier.
@Repository
interface MovieRepository extends CrudRepository<Movie, Long> {
  
  @Find
  Page<Movie> findAll(PageRequest pageRequest, Sort<Movie> sort);

  /// Retrieves a movie by its corresponding external TMDB identifier.
  ///
  /// @param tmdbId the external TMDB identifier
  /// @return an [Optional] containing the movie if found, or empty if it does not exist
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Movie> findByTmdbId(Integer tmdbId);
}
