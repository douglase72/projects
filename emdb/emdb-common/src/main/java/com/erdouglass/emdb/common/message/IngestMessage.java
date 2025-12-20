package com.erdouglass.emdb.common.message;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngestMessage(
    @NotNull UUID id,
    @NotNull Instant timestamp,
    @NotNull @Positive Integer tmdbId) {
  
  public static IngestMessage of(UUID id, Integer tmdbId) {
    return new IngestMessage(id, Instant.now(), tmdbId);
  }

}
