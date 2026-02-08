package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.Movie;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {
  
  @Query("WHERE tmdbId = :tmdbId")
  Optional<Movie> findByTmdbId(Integer tmdbId);
  
}
