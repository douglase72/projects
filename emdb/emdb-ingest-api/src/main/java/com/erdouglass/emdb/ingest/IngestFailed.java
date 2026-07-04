package com.erdouglass.emdb.ingest;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.MediaType;

public record IngestFailed(
    @NotNull UUID id,
    @NotNull Instant createdAt,
    @NotNull String message,
    @NotNull MediaType type) implements IngestEvent {
  
  public IngestFailed(UUID id, String message, MediaType type) {
    this(id, Instant.now(), message, type);
  }
}
