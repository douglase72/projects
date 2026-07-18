package com.erdouglass.emdb.media.domain.movie;

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
