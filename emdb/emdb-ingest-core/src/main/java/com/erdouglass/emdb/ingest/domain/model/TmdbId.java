package com.erdouglass.emdb.ingest.domain.model;

import java.util.Objects;

public record TmdbId(Integer value) {

  public TmdbId {
    Objects.requireNonNull(value, "tmdb id must not be null");
    if (value < 1) {
      throw new IllegalArgumentException("tmdb id must be positive");
    }
  }
  
  public static TmdbId of(Integer id) {
    return new TmdbId(id);
  }
  
  @Override
  public String toString() {
    return value.toString();
  }
}
