package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.util.List;

import jakarta.data.Limit;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Repository;

@Repository
public interface JakartaDataCommandOutboxRepository {

  @Insert
  void insert(IngestCommandOutboxEntity command);
  
  @Delete
  void delete(IngestCommandOutboxEntity command);
  
  @Find
  @OrderBy("id")
  List<IngestCommandOutboxEntity> findAll(Limit limit);
}
