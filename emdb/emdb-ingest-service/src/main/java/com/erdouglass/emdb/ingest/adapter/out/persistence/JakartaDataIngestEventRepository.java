package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;

@Repository
interface JakartaDataIngestEventRepository {

  @Insert
  void insert(IngestEventEntity event);
  
  @Find
  Optional<IngestEventEntity> findById(UUID id);
}
