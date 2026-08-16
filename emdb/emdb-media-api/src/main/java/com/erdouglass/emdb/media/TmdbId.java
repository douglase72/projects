package com.erdouglass.emdb.media;

import java.util.Objects;

/// The identifier a title carries in *The Movie Database*.
///
/// This is the natural key of the movie aggregate: it originates outside this
/// system, is stable for the life of the title, and is what ingestion upserts
/// on. It is not the catalogue id and is never used to address a movie over the
/// public API except on the ingestion endpoint.
///
/// Rejects `null` and any value below `1`; TMDB issues no zero or negative ids.
///
/// @param value the TMDB id, never `null` and always positive
public record TmdbId(Integer value) {

  public TmdbId {
    Objects.requireNonNull(value, "tmdb id must not be null");
    if (value < 1) {
      throw new IllegalArgumentException("tmdb id must be positive");
    }
  }
  
  public static TmdbId of(Integer tmdbId) {
    return new TmdbId(tmdbId);
  }
}
