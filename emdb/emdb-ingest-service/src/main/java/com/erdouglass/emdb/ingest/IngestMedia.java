package com.erdouglass.emdb.ingest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/// Command instructing the ingest pipeline to fetch and persist a single
/// piece of media from TMDB.
///
/// Published by [com.erdouglass.emdb.ingest.movie.MovieScheduler] (and
/// future siblings) onto the ingest-media queue, then consumed and
/// dispatched by [IngestConsumer]. The [IngestSource] is informational —
/// it lets logs and dead-letter inspection distinguish a scheduled run
/// from a manually-triggered one.
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
