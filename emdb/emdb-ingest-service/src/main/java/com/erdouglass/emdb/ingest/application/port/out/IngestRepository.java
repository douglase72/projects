package com.erdouglass.emdb.ingest.application.port.out;

import java.util.Optional;

import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface IngestRepository {

  void save(Ingest job);
  
  Optional<Ingest> findById(IngestId id);
}
