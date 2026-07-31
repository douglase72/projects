package com.erdouglass.emdb.ingest.adapter.inbound.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.ingest.domain.model.IngestType;

public record IngestMediaRequest(
    @NotBlank String source,
    @NotBlank String sourceId,
    @NotNull IngestType type) {
  
  public static IngestMediaRequest of(String source, String sourceId, IngestType type) {
    return new IngestMediaRequest(source, sourceId, type);
  }
}
