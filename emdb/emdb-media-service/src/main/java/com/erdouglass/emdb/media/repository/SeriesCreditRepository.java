package com.erdouglass.emdb.media.repository;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.SeriesCredit;

@Repository
public interface SeriesCreditRepository extends CrudRepository<SeriesCredit, UUID> {
  
  @Query("""
      SELECT c
      FROM SeriesCredit c JOIN FETCH c.person p JOIN FETCH c.series s JOIN FETCH c.roles r
      WHERE s.id = :id
      ORDER BY c.totalEpisodes DESC, c.order
      """)
  List<SeriesCredit> findBySeriesId(Long id);

}
