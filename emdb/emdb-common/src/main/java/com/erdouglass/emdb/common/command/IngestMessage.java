package com.erdouglass.emdb.common.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngestMessage(
    @NotBlank String jobId,
    @NotNull @Positive Integer tmdbId) {
  
  public static IngestMessage of(String jobId, Integer tmdbId) {
    return new IngestMessage(jobId, tmdbId);
  }

}
