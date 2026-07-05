package com.erdouglass.emdb.media.core.series;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository(dataStore = "media")
interface SeriesCreditRepository extends CrudRepository<SeriesCredit, UUID> {

  @Query("""
      SELECT DISTINCT c FROM SeriesCredit c
      JOIN FETCH c.person
      LEFT JOIN FETCH c.roles r
      WHERE c.series.id = :seriesId
      ORDER BY c.totalEpisodes DESC, c.order, r.episodeCount DESC
      """)
  List<SeriesCredit> findBySeriesId(Long seriesId);  
  
  @Query("DELETE FROM SeriesCredit WHERE series = :series")
  void deleteBySeries(Series series);
}
