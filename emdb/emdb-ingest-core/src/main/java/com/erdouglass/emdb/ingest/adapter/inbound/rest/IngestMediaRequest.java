package com.erdouglass.emdb.ingest.adapter.inbound.rest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.media.MediaType;

public record IngestMediaRequest(
    @NotNull @Positive Integer tmdbId,
    @NotNull MediaType mediaType) {
  
  public static IngestMediaRequest of(Integer tmdbId, MediaType mediaType) {
    return new IngestMediaRequest(tmdbId, mediaType);
  }
}
