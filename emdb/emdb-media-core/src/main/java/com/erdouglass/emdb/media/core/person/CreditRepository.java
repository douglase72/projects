package com.erdouglass.emdb.media.core.person;

import java.util.List;

import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.core.movie.MovieCredit;
import com.erdouglass.emdb.media.core.series.SeriesCredit;

@Repository(dataStore = "media")
interface CreditRepository {
  
  /// Finds a person's movie credits with the movie eagerly fetched, ordered by
  /// the movie's score descending.
  ///
  /// @param personId the person id
  /// @return the person's movie credits
  @Query("""
      SELECT mc FROM MovieCredit mc
      JOIN FETCH mc.movie m
      WHERE mc.person.id = :personId
      ORDER BY m.score DESC
      """)
  List<MovieCredit> findMovieCredits(Long personId);

  /// Finds a person's series credits with the series and its roles eagerly
  /// fetched, ordered by the series' score descending. {@code DISTINCT} guards
  /// against duplicate credits produced by the roles join.
  ///
  /// @param personId the person id
  /// @return the person's series credits
  @Query("""
      SELECT DISTINCT sc FROM SeriesCredit sc
      JOIN FETCH sc.series s
      LEFT JOIN FETCH sc.roles
      WHERE sc.person.id = :personId
      ORDER BY s.score DESC
      """)
  List<SeriesCredit> findSeriesCredits(Long personId);
}
