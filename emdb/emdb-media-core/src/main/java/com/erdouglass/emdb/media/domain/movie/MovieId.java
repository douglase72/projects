package com.erdouglass.emdb.media.domain.movie;

import java.util.Objects;
import java.util.UUID;

import com.erdouglass.emdb.media.domain.shared.PublicId;

/// Internal surrogate identity of a [Movie].
///
/// Application-generated (UUIDv7), used for persistence joins and nothing
/// else. Must never appear in URLs, payloads, or logs shown to users —
/// that job belongs to [PublicId]. Value object: immutable, self-validating,
/// equal by value.
public record MovieId(UUID value) {

  public MovieId {
    Objects.requireNonNull(value, "movie id must not be null");
  }
  
  public static MovieId of(UUID id) {
    return new MovieId(id);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
