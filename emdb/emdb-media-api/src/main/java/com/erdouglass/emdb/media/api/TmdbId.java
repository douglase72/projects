package com.erdouglass.emdb.media.api;

import java.util.Objects;

public record TmdbId(Integer value) {
  
  public TmdbId {
    Objects.requireNonNull(value, "id is required");
    if (value <= 0) throw new IllegalArgumentException("TMDB id must be positive");
  }
  
  public static TmdbId of(Integer id) { return new TmdbId(id); }
}
