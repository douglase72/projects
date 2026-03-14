package com.erdouglass.emdb.media.repository;

import java.util.List;
import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

  @Query("WHERE tmdbId = :tmdbId")
  Optional<Person> findByTmdbId(Integer tmdbId);
  
  @Query("WHERE tmdbId IN :tmdbIds")
  List<Person> findByTmdbIdIn(List<Integer> tmdbIds);  
  
}
