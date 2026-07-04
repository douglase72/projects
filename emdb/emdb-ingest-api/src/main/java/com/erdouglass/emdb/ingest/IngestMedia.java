package com.erdouglass.emdb.ingest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.media.MediaType;

/// Command requesting ingest of a single TMDB entity.
///
/// Carries the TMDB identifier, the kind of entity being ingested, and the
/// origin of the request so downstream components can react appropriately (e.g.
/// logging, metrics, prioritization).
///
/// @param tmdbId the TMDB identifier of the entity to ingest; must be positive
/// @param type   the kind of entity to ingest
/// @param source the origin of the ingest request
public record IngestMedia(
    @NotNull @Positive Integer tmdbId, 
    @NotNull MediaType type, 
    @NotNull Source source) {

  public static final String START_TIME = "start-time";

  /// Convenience factory equivalent to invoking the canonical constructor.
  ///
  /// @param tmdbId the TMDB identifier of the entity to ingest
  /// @param type   the kind of entity to ingest
  /// @param source the origin of the ingest request
  /// @return a new [IngestMedia] instance with the given values
  public static IngestMedia of(Integer tmdbId, MediaType type, Source source) {
    return new IngestMedia(tmdbId, type, source);
  }

  public enum Source {
    CLI, 
    MEDIA, 
    SCHEDULER, 
    UI;
  }  
}
