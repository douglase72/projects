package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;

@Repository
public interface JakartaDataIngestEventRepository {

  @Insert
  void insert(IngestEventEntity entity);
  
  @Update
  void update(IngestEventEntity entity);
  
  @Find
  List<IngestEventEntity> findAll();
  
  @Find
  IngestEventEntity findById(UUID id);
  
  @Query("where published = false order by id")
  List<IngestEventEntity> findUnpublished();
}
