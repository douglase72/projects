package com.erdouglass.emdb.common.command;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngestCommand(
    @NotBlank String traceId,
    @NotNull Instant timestamp,
    @NotNull EventStatus status,
    @NotNull @Positive Integer tmdbId) {
  
  public static IngestCommand of(String traceId, EventStatus status, Integer tmdbId) {
    return new IngestCommand(traceId, Instant.now(), status, tmdbId);
  }

}
