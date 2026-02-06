package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.repository.Find;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;

import com.erdouglass.emdb.media.entity.Movie;

@Repository
public interface MovieRepository {
  
  @Save
  Movie save(Movie movie);
  
  @Find
  Optional<Movie> findById(Long id);
  
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Movie> findByTmdbId(Integer tmdbId);
  
}
