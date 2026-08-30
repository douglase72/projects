package com.erdouglass.emdb.ingest.application.port.in;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface SubmitIngestUseCase {
  
  IngestId submit(IngestMediaCommand command);
}
