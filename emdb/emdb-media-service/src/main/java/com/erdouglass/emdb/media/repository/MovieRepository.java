package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.api.query.MovieView;
import com.erdouglass.emdb.media.entity.Movie;

/// Data access layer for `Movie` entities.
///
/// Extends `CrudRepository` to provide standard create, read, update, 
/// and delete operations.
@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {
  
  /// Returns a paginated list of [MovieView] projections sorted by score
  /// descending, with id as a tiebreaker.
  ///
  /// This method uses a JPQL `SELECT NEW` projection to select only the columns
  /// needed for list display, avoiding the overhead of loading full
  /// [Movie] entities with unused fields like budget, revenue, and credits.
  ///
  /// The `CAST(m.backdrop AS STRING)` and `CAST(m.poster AS STRING)` expressions
  /// convert the UUID columns directly in the query, eliminating an intermediate
  /// record with UUID fields.
  ///
  /// Sorting is embedded in the `@Query` rather than passed as an `Order<Movie>`
  /// parameter because Hibernate's annotation processor requires entity return
  /// types when validating dynamic sort attributes. Jakarta Data 1.1 is expected
  /// to resolve this with native record projection support via `@Find`.
  ///
  /// @param pageRequest the page request controlling offset and page size
  /// @return a [Page] of [MovieView] projections
  @Query("""
      SELECT NEW com.erdouglass.emdb.media.api.query.MovieView(
          m.id, m.title, m.releaseDate, m.score, 
          CAST(m.backdrop AS STRING), CAST(m.poster AS STRING), 
          m.overview)
      FROM Movie m
      ORDER BY m.releaseDate DESC, m.id ASC
      """)
  Page<MovieView> find(PageRequest pageRequest);

  /// Retrieves a movie by its corresponding external TMDB identifier.
  ///
  /// @param tmdbId the external TMDB identifier
  /// @return an [Optional] containing the movie if found, or empty if it does not exist
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Movie> findByTmdbId(Integer tmdbId);
}
