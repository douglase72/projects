package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;

@Repository
interface JakartaDataIngestJobRepository {

  @Save
  void save(IngestJobEntity job);
  
  @Find
  Optional<IngestJobEntity> findById(UUID id);
}
