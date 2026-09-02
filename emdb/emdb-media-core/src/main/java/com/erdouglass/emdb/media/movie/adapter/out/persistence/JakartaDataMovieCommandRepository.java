package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.Optional;

import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

@Repository(dataStore = "media")
interface JakartaDataMovieCommandRepository {

  @Insert
  MovieEntity insert(MovieEntity entity);
  
  @Update
  MovieEntity update(MovieEntity entity);
  
  @Find
  Optional<MovieEntity> findByTmdbIdId(Integer tmdbId);
}
