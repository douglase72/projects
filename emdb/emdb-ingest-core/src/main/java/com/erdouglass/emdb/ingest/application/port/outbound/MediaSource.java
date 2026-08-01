package com.erdouglass.emdb.ingest.application.port.outbound;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.ingest.domain.model.IngestSource;

public interface MediaSource {

  Media extract(@NotNull IngestSource source);
}
