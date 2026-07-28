package com.erdouglass.emdb.ingest.adapter.inbound.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.MediaType;

public record IngestMediaRequest(
    @NotBlank String source,
    @NotBlank String sourceId,
    @NotNull MediaType mediaType) {
  
  public static IngestMediaRequest of(String source, String sourceId, MediaType mediaType) {
    return new IngestMediaRequest(source, sourceId, mediaType);
  }
}
