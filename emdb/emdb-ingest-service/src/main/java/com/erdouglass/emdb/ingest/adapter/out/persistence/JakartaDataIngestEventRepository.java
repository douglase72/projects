package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.data.Limit;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository
public interface JakartaDataIngestEventRepository {

  @Insert
  void insert(IngestEventEntity entity);
  
  @Query("update IngestEventEntity set published = true where id = :id")
  void markPublished(UUID id);
  
  @Find
  List<IngestEventEntity> findAll();
  
  @Find
  Optional<IngestEventEntity> findById(UUID id);
  
  @Query("where published = false order by id")
  List<IngestEventEntity> findUnpublished(Limit limit);
}
