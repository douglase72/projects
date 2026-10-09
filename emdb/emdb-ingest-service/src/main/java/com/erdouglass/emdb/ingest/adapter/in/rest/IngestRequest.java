package com.erdouglass.emdb.ingest.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngestRequest(
    @NotNull @Positive Integer tmdbId, 
    @NotBlank String mediaType) {

  public static IngestRequest of(Integer tmdbId, String mediaType) {
    return new IngestRequest(tmdbId, mediaType);
  }
}