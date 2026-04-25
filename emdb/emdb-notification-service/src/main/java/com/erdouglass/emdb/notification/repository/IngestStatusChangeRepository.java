package com.erdouglass.emdb.notification.repository;

import java.util.List;
import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.notification.entity.IngestStatusChange;

/// JPA repository for [IngestStatusChange] history rows.
@Repository
public interface IngestStatusChangeRepository extends CrudRepository<IngestStatusChange, Long> {
  
  /// Returns every status change for the given ingest, oldest first.
  ///
  /// Ascending order matches the UI's chronological history view.
  ///
  /// @param id the ingest correlation ID
  /// @return the status changes for the ingest, ordered by lastModified ascending  
  @Query("WHERE ingest.id = :id ORDER BY lastModified ASC")
  List<IngestStatusChange> findByIngestId(UUID id);
}
