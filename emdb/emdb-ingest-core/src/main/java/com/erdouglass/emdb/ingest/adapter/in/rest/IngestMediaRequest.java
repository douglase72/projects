package com.erdouglass.emdb.ingest.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngestMediaRequest(
    @NotNull @Positive Integer tmdbId,
    @NotBlank String type) {
  
  public static IngestMediaRequest of(Integer tmdbId, String type) {
    return new IngestMediaRequest(tmdbId, type);
  }
}
