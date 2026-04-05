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
      FROM MovieCredit mc 
      JOIN FETCH mc.person p 
      JOIN FETCH mc.movie m
      WHERE m.id = :id
      ORDER BY mc.order
  """)
  List<MovieCredit> findByMovieId(Long id);
  
  @Query("""
      FROM MovieCredit mc 
      LEFT JOIN FETCH mc.movie m
      WHERE mc.person.id = :id
      ORDER BY m.score DESC, mc.created
  """)
  List<MovieCredit> findByPersonId(Long id);
  
  @Query("DELETE FROM MovieCredit c WHERE c.person.tmdbId IN :tmdbIds")
  int deleteByTmdbIdIn(List<Integer> tmdbIds);
  
}
