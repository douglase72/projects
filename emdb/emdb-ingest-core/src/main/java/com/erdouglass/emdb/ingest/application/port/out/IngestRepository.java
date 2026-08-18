package com.erdouglass.emdb.ingest.application.port.out;

import java.util.Optional;

import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface IngestRepository {
  
  void save(Ingest ingest);
  
  Optional<Ingest> findById(IngestId id);
}
