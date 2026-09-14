package com.erdouglass.emdb.media.person.adapter.out.persistence;

import java.util.List;

import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;

@Repository
public interface JakartaDataPersonOutboxRepository {

  @Insert
  void insertAll(List<PersonOutboxEntity> entities);
  
  @Delete
  void deleteAll(List<PersonOutboxEntity> entities);
  
  @Find
  List<PersonOutboxEntity> findAll();
}
