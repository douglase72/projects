package com.erdouglass.emdb.ingest.application.port.inbound;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface IngestMediaUseCase {

  /// Accepts an ingest request: persists a new {@code Ingest} in SUBMITTED state
  /// (recording the IngestSubmitted event) and enqueues it for asynchronous,
  /// one-at-a-time processing.
  /// 
  /// @return the ingest id
  IngestId ingest(@NotNull IngestMediaCommand command);
}
