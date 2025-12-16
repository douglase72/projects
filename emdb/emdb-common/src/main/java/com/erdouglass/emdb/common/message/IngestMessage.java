package com.erdouglass.emdb.common.message;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngestMessage(
    @NotBlank String jobId,
    @NotNull Instant timestamp,
    @NotNull @Positive Integer tmdbId) {
  
  public static IngestMessage of(String jobId, Integer tmdbId) {
    return new IngestMessage(jobId, Instant.now(), tmdbId);
  }

}
