package com.erdouglass.emdb.ingest.application.port.out;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface IngestCommandQueue {
  
  void enqueue(@NotNull IngestId id);
}
