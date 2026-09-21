package com.erdouglass.emdb.ingest.application.port.out;

import java.util.Optional;

import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.IngestJob;

public interface IngestJobRepository {

  void save(IngestJob job);
  
  Optional<IngestJob> findById(IngestId id);
}
