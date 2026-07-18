package com.erdouglass.emdb.media.domain.movie;

/// Public, URL-facing identity of a [Movie].
///
/// Database-assigned sequence, rendered as `mv_{n}`. This is the only movie
/// identity adapters may expose; it exists precisely so [MovieId] never has
/// to. Absent (null field on the aggregate) until first persistence — the
/// one identity the hexagon does not mint itself.
public record PublicId(Long value) {
  
  public PublicId {
    if (value == null) {
      throw new IllegalArgumentException("movie id must not be null");
    }
    if (value < 1) {
      throw new IllegalArgumentException("movie id must be positive");
    }
  }

  @Override
  public String toString() {
    return "mv_" + value;
  }
}
