package com.erdouglass.emdb.notification.ingest;

import java.util.UUID;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

@Repository(dataStore = "notification")
interface IngestRepository extends CrudRepository<Ingest, UUID> {

}
