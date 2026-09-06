package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;
import java.util.UUID;

import com.erdouglass.emdb.media.kernel.AggregateId;

public record MovieId(UUID value) implements AggregateId {
  
  public MovieId {
    Objects.requireNonNull(value, "movie id must not be null");
  }
  
  public static MovieId newId() { return new MovieId(UUID.randomUUID()); }
  public static MovieId of(UUID id) { return new MovieId(id); }
}
