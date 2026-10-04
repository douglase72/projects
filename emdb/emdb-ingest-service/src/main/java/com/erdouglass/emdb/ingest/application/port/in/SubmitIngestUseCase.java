package com.erdouglass.emdb.ingest.application.port.in;

import com.erdouglass.emdb.shared.kernel.PublicId;

public interface SubmitIngestUseCase {
  
  PublicId submit(IngestMediaCommand command);
}
