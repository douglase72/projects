package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import jakarta.data.Limit;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository
public interface JakartaDataIngestCommandRepository {

  @Insert
  void insert(IngestCommandEntity entity);
  
  @Query("update IngestCommandEntity set published = true where id = :id")
  void markPublished(UUID id);
  
  @Query("where published = false order by id")
  List<IngestCommandEntity> findUnpublished(Limit limit);
}
