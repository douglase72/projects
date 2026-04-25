package com.erdouglass.emdb.notification.repository;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.notification.entity.IngestStatusChange;

@Repository
public interface IngestStatusChangeRepository extends CrudRepository<IngestStatusChange, Long> {
  
  @Query("WHERE ingest.id = :id ORDER BY lastModified ASC")
  List<IngestStatusChange> findByIngestId(UUID id);
}
