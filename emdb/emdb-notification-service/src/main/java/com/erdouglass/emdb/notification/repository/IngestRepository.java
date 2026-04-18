package com.erdouglass.emdb.notification.repository;

import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.notification.entity.Ingest;
import com.erdouglass.emdb.notification.entity.StatusEvent;

@Repository
public interface IngestRepository extends CrudRepository<Ingest, UUID> {

  @Insert
  StatusEvent insertStatus(StatusEvent event);
}
