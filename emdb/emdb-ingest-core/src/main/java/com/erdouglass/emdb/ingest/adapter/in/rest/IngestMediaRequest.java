package com.erdouglass.emdb.ingest.adapter.in.rest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.ingest.domain.model.IngestType;

public record IngestMediaRequest(
    @NotNull @Positive Integer tmdbId,
    @NotNull IngestType type) {
  
  public static IngestMediaRequest of(Integer tmdbId, IngestType type) {
    return new IngestMediaRequest(tmdbId, type);
  }
}
