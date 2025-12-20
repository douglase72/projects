package com.erdouglass.emdb.media.repository;

import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.Movie;

@Repository
public interface MovieRepository {

  @Insert
  Movie insert(Movie movie);
  
}
