package com.erdouglass.emdb.ingest.application.port.in;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface ExecuteIngestUseCase {
  
  void execute(IngestId id);
}
