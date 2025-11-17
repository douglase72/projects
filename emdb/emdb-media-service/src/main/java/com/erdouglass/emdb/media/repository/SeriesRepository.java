package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

import com.erdouglass.emdb.media.entity.Series;

@Repository
public interface SeriesRepository {
  
  @Insert
  Series insert(Series series);
  
  @Find
  Optional<Series> findById(Long id);
  
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Series> findByTmdbId(Integer tmdbId);
  
  @Update
  Series update(Series series);
  
  @Delete
  void deleteById(Long id);

}
