package com.erdouglass.emdb.ingest.application.port.outbound;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface IngestProducer {

  void publish(@NotNull IngestId id);
}
