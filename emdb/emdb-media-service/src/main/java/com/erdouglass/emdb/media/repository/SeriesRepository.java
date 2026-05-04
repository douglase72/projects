package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.api.query.SeriesView;
import com.erdouglass.emdb.media.entity.Series;

@Repository
public interface SeriesRepository extends CrudRepository<Series, Long> {
  
  @Query("""
      SELECT NEW com.erdouglass.emdb.media.api.query.SeriesView(
          s.id, s.title, s.firstAirDate, s.score, 
          CAST(s.backdrop AS STRING), CAST(s.poster AS STRING), 
          s.overview)
      FROM Series s
      ORDER BY s.score DESC, s.id ASC
      """)
  Page<SeriesView> find(PageRequest pageRequest);

  /// Retrieves a Series by its corresponding external TMDB identifier.
  ///
  /// @param tmdbId the external TMDB identifier
  /// @return an [Optional] containing the series if found, or empty if it does not exist
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Series> findByTmdbId(Integer tmdbId);
}
