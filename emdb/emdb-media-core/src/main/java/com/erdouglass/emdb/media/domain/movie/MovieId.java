package com.erdouglass.emdb.media.domain.movie;

import java.util.UUID;

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
