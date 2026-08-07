package com.erdouglass.emdb.media;

import java.util.Objects;

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
