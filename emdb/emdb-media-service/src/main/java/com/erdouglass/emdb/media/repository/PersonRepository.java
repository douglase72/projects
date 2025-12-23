package com.erdouglass.emdb.media.repository;

import java.util.List;
import java.util.Optional;

import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

import com.erdouglass.emdb.media.entity.Person;

@Repository
public interface PersonRepository {
  
  @Insert
  Person insert(Person person);
  
  @Insert
  List<Person> insertAll(List<Person> people);
  
  @Find
  Optional<Person> findById(Long id);
  
  @Query("WHERE tmdbId IN :tmdbIds")
  List<Person> findByTmdbIdIn(List<Integer> tmdbIds);
  
  @Update
  Person update(Person person);
  
  @Delete
  void deleteById(Long id);

}
