package com.erdouglass.emdb.media.repository;

import java.util.Optional;

import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

import com.erdouglass.emdb.media.entity.Person;

@Repository
public interface PersonRepository {
  
  @Insert
  Person insert(Person person);
  
  @Find
  Optional<Person> findById(Long id);
  
  @Update
  Person update(Person person);
  
  @Delete
  void deleteById(Long id);

}
