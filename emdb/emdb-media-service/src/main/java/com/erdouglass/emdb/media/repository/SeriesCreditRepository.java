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
      FROM SeriesCredit sc 
      JOIN FETCH sc.person p 
      JOIN FETCH sc.series s 
      JOIN FETCH sc.roles r
      WHERE s.id = :id
      ORDER BY sc.totalEpisodes DESC, sc.order
      """)
  List<SeriesCredit> findBySeriesId(Long id);
  
  @Query(""" 
      FROM SeriesCredit sc 
      LEFT JOIN FETCH sc.series
      LEFT JOIN FETCH sc.roles
      WHERE sc.person.id = :id
      ORDER BY sc.totalEpisodes DESC, sc.order
  """)
  List<SeriesCredit> findByPersonId(Long id);

}
