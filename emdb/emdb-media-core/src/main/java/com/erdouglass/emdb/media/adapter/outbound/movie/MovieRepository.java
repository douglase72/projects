package com.erdouglass.emdb.media.adapter.outbound.movie;

import java.util.Optional;
import java.util.UUID;

import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

@Repository(dataStore = "media")
interface MovieRepository {

  @Insert
  MovieEntity insert(MovieEntity entity);
  
  @Update
  MovieEntity update(MovieEntity entity);
  
  @Find
  Optional<MovieEntity> findById(UUID id);
  
  @Find
  Optional<MovieEntity> findBySourceId(String source, String sourceId);
}
