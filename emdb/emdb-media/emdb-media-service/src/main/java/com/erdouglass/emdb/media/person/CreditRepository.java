package com.erdouglass.emdb.media.person;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.credit.Credit;
import com.erdouglass.emdb.media.movie.MovieCredit;
import com.erdouglass.emdb.media.series.SeriesCredit;

/// Jakarta Data repository for reading a person's credits. Movie and series
/// credits are queried separately so each subtype's associations can be
/// fetch-joined eagerly — the movie for [MovieCredit], the series and its roles
/// for [SeriesCredit] — avoiding lazy initialization during DTO mapping.
@Repository
interface CreditRepository extends CrudRepository<Credit, UUID> {
  
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
  List<MovieCredit> findMovieCreditsByPersonId(Long personId);
  
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
  List<SeriesCredit> findSeriesCreditsByPersonId(Long personId);
}
