package com.erdouglass.emdb.ingest.application.port.outbound;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface IngestRepository {
  
  void save(@NotNull Ingest ingest);
  
  Optional<Ingest> findById(@NotNull IngestId id);
}
