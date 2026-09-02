package com.erdouglass.emdb.media.person.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

@Repository(dataStore = "media")
interface JakartaDataPersonCommandRepository {
  
  @Insert
  PersonEntity insert(PersonEntity entity);
  
  @Update
  PersonEntity update(PersonEntity entity);
  
  @Find
  Optional<PersonEntity> findByTmdbIdId(Integer tmdbId);
}
