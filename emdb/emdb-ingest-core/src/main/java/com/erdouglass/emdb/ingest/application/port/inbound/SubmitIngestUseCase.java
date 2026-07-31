package com.erdouglass.emdb.ingest.application.port.inbound;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface SubmitIngestUseCase {

  IngestId submit(@NotNull IngestMediaCommand command);
}
