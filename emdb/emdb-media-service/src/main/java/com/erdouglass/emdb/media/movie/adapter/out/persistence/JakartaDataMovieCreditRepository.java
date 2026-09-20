package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

@Repository
interface JakartaDataMovieCreditRepository {
  
  @Insert
  void insertAll(List<MovieCreditEntity> credits);
  
  @Update
  void updateAll(List<MovieCreditEntity> credits);
  
  @Query("delete from MovieCreditEntity c where c.movie.id = :movieId and c.id in :ids")
  void deleteByMovieId(UUID movieId, List<UUID> ids);
  
  @Query("select c.id from MovieCreditEntity c where c.movie.id = :movieId")
  List<UUID> findByMovieId(UUID movieId);
}
