package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.repository.Find;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;

import com.erdouglass.emdb.media.entity.Series;

@Repository
public interface SeriesRepository {

  @Save
  Series save(Series series);
  
  @Find
  Optional<Series> findById(Long id);
  
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Series> findByTmdbId(Integer tmdbId);
  
}
