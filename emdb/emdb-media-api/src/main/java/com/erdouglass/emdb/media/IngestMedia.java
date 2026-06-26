package com.erdouglass.emdb.media;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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
    @NotNull Type type, 
    @NotNull Source source) {

  public static final String START_TIME = "start-time";

  /// Convenience factory equivalent to invoking the canonical constructor.
  ///
  /// @param tmdbId the TMDB identifier of the entity to ingest
  /// @param type   the kind of entity to ingest
  /// @param source the origin of the ingest request
  /// @return a new [IngestMedia] instance with the given values
  public static IngestMedia of(Integer tmdbId, Type type, Source source) {
    return new IngestMedia(tmdbId, type, source);
  }

  public enum Type {
    MOVIE("movie"), 
    PERSON("person"), 
    SERIES("series");

    private final String type;

    Type(String type) {
      this.type = type;
    }

    @Override
    public String toString() {
      return type;
    }
  }

  public enum Source {
    CLI, 
    MEDIA, 
    SCHEDULER, 
    UI;
  }  
}
