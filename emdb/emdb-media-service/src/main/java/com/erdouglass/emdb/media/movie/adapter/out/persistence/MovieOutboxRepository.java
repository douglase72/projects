package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.data.Limit;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Repository;

@Repository
public interface MovieOutboxRepository {

  @Insert
  void insert(MovieOutboxEntity entity);
  
  @Delete
  void delete(MovieOutboxEntity entity);
  
  @Find
  @OrderBy("id")
  List<MovieOutboxEntity> findAll(Limit limit);
  
  @Find
  Optional<MovieOutboxEntity> findById(UUID id); 
}
