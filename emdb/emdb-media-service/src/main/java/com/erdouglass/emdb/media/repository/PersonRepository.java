package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.entity.Person;

@Repository
public interface PersonRepository {
  
  @Insert
  Person insert(Person person);
  
  @Find
  Optional<Person> findById(Long id);

}
