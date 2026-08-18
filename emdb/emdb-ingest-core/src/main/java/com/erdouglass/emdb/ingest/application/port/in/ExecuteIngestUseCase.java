package com.erdouglass.emdb.ingest.application.port.in;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface ExecuteIngestUseCase {

  void execute(@NotNull IngestId id);
}
