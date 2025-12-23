package com.erdouglass.emdb.media.repository;

import java.util.List;

import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

import com.erdouglass.emdb.media.entity.MovieCredit;

@Repository
public interface MovieCreditRepository {
  
  @Insert
  List<MovieCredit> insertAll(List<MovieCredit> credits);
  
  @Query("""
      SELECT c
      FROM MovieCredit c JOIN FETCH c.person p JOIN FETCH c.movie m
      WHERE m.id = :id
      ORDER BY c.order
      """)
  List<MovieCredit> findAll(Long id);
  
  @Update
  List<MovieCredit> updateAll(List<MovieCredit> credits);
  
  @Query("DELETE FROM MovieCredit c WHERE c.id IN :ids")
  int deleteByIdIn(List<Long> ids);
  
  @Query("DELETE FROM MovieCredit c WHERE c.person.tmdbId IN :tmdbIds")
  int deleteByTmdbIdIn(List<Integer> tmdbIds);

}
