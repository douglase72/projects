package com.erdouglass.emdb.ingest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngestMedia(
    @NotNull @Positive Integer tmdbId,
    @NotNull MediaType type,
    @NotNull IngestSource source) {
  
  public enum IngestSource {
    CLI,
    SCHEDULER,
    UI;
  }
  
  public static IngestMedia of(Integer tmdbId, MediaType type, IngestSource source) {
    return new IngestMedia(tmdbId, type, source);
  }    
}
