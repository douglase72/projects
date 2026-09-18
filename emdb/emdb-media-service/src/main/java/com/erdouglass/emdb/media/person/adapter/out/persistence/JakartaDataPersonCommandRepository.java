package com.erdouglass.emdb.media.person.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

@Repository
interface JakartaDataPersonCommandRepository {

  @Insert
  PersonEntity insert(PersonEntity entity);
  
  @Insert
  List<PersonEntity> insertAll(List<PersonEntity> people);
  
  @Update
  PersonEntity update(PersonEntity entity);
  
  @Delete
  void deleteById(UUID id);
  
  @Find
  Optional<PersonEntity> findById(UUID id);
  
  @Find
  Optional<PersonEntity> findByTmdbId(Integer tmdbId);
  
  @Query("WHERE tmdbId IN :tmdbIds")
  List<PersonEntity> findByTmdbIdIn(List<Integer> tmdbIds);
}
