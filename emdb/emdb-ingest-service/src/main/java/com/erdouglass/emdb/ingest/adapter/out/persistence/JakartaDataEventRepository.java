package com.erdouglass.emdb.ingest.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

@Repository
interface JakartaDataEventRepository {

  @Query("""
      insert into EventEntity (id, processedAt)
      values (:id, :processedAt)
      on conflict(id) do nothing
      """)
  boolean insertIfAbsent(UUID id, Instant processedAt);
}
