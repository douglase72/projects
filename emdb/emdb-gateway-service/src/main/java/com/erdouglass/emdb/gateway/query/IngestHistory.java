package com.erdouglass.emdb.gateway.query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.common.event.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatus;

public record IngestHistory(
    @NotNull UUID id,
    @NotNull List<IngestStatusChange> changes) {
  
  public record IngestStatusChange(
      @NotNull IngestStatus status,
      @NotNull Instant lastModified,
      @NotNull IngestSource source,
      @NotNull String message) {}
}
