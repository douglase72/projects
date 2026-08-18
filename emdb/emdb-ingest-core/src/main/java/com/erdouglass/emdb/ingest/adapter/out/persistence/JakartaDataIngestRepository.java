package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;

@Repository(dataStore = "ingest")
interface JakartaDataIngestRepository {
  
  @Save
  void save(IngestEntity ingest);
  
  @Find
  Optional<IngestEntity> findById(UUID id);
  
  @Insert
  void insert(IngestEventEntity events);
}
