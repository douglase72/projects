package com.erdouglass.emdb.common.api.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.common.api.MediaType;

/// Command to ingest a media entry from TMDB into the database.
///
/// Published to the message broker by the gateway and consumed by the
/// media-service. The [#tmdbId()] and [#type()] identify what to extract
/// from TMDB, while [#source()] records where the request originated.
public record IngestMedia(
    @NotNull @Positive Integer tmdbId,
    @NotNull MediaType type,
    @NotNull IngestSource source) {
  public static final String INGEST_KEY = "ingest.media";
  public static final String START_TIME = "start-time";
  
  public enum IngestSource {
    CLI,
    SCHEDULER,
    UI;
  }
  
  public static IngestMedia of(Integer tmdbId, MediaType type, IngestSource source) {
    return new IngestMedia(tmdbId, type, source);
  }  
}
