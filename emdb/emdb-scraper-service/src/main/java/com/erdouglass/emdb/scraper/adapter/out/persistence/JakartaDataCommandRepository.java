package com.erdouglass.emdb.scraper.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;

@Repository
interface JakartaDataCommandRepository {

  @Insert
  void insert(CommandEntity entity);
  
  @Find
  Optional<CommandEntity> findById(UUID id); 
}
