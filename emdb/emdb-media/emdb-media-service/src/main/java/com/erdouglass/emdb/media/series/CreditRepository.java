package com.erdouglass.emdb.media.series;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

/// Jakarta Data repository for [SeriesCredit] persistence, adding bulk removal of
/// every credit attached to a given series.
@Repository
interface CreditRepository extends CrudRepository<SeriesCredit, UUID> {
  
  @Query("""
      SELECT DISTINCT c FROM SeriesCredit c
      JOIN FETCH c.person
      LEFT JOIN FETCH c.roles r
      WHERE c.series.id = :seriesId
      ORDER BY c.totalEpisodes DESC, c.order, r.episodeCount DESC
      """)
  List<SeriesCredit> findBySeriesId(Long seriesId);  

  /// Deletes all credits associated with the given series.
  ///
  /// @param series the series whose credits should be removed
  @Query("DELETE FROM SeriesCredit WHERE series = :series")
  void deleteBySeries(Series series);
}
