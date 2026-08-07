package com.erdouglass.emdb.media.domain.movie;

import java.util.Objects;
import java.util.UUID;

public record MovieId(UUID value) {

  public MovieId {
    Objects.requireNonNull(value, "movie id must not be null");
  }
  
  public static MovieId of(UUID id) {
    return new MovieId(id);
  }
}
