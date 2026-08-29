package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
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
  
  @Insert
  void insertCredits(List<MovieCreditEntity> credits);
  
  @Update
  void updateCredits(List<MovieCreditEntity> credits);
  
  @Query("delete from MovieCreditEntity c where c.movie.id = :movieId and c.id in :ids")
  void deleteCredits(Long movieId, List<UUID> ids);
  
  @Query("select c.id from MovieCreditEntity c where c.movie.id = :movieId")
  List<UUID> findCreditIds(Long movieId);
}
