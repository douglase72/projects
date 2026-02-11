package com.erdouglass.emdb.media.repository;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.MovieCredit;

@Repository
public interface MovieCreditRepository extends CrudRepository<MovieCredit, UUID> {
  
  @Query("""
      SELECT c
      FROM MovieCredit c JOIN FETCH c.person p JOIN FETCH c.movie m
      WHERE m.id = :id
      ORDER BY c.order
      """)
  List<MovieCredit> findAll(Long id); 
  
  @Query("DELETE FROM MovieCredit c WHERE c.person.tmdbId IN :tmdbIds")
  int deleteByTmdbIdIn(List<Integer> tmdbIds);

}
