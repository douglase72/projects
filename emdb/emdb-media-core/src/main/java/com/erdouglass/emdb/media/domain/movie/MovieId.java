package com.erdouglass.emdb.media.domain.movie;

import java.util.UUID;

/// Internal surrogate identity of a [Movie].
///
/// Application-generated (UUIDv7), used for persistence joins and nothing
/// else. Must never appear in URLs, payloads, or logs shown to users —
/// that job belongs to [PublicId]. Value object: immutable, self-validating,
/// equal by value.
public record MovieId(UUID value) {

  public MovieId {
    if (value == null) {
      throw new IllegalArgumentException("movie id must not be null");
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
