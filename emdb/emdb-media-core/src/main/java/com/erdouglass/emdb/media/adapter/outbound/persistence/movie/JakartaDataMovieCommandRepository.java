package com.erdouglass.emdb.media.adapter.outbound.persistence.movie;

import java.util.Optional;

import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

@Repository(dataStore = "media")
public interface JakartaDataMovieCommandRepository {

  @Insert
  MovieEntity insert(MovieEntity entity);
  
  @Update
  MovieEntity update(MovieEntity entity);
  
  @Delete
  void deleteById(Long id);
  
  @Find
  Optional<MovieEntity> findById(Long id);
  
  @Find
  Optional<MovieEntity> findByTmdbId(Integer tmdbId);
}
