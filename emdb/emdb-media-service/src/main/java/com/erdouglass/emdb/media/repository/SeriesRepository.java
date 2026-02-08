package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.Series;

@Repository
public interface SeriesRepository extends CrudRepository<Series, Long> {
  
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Series> findByTmdbId(Integer tmdbId);
  
}
