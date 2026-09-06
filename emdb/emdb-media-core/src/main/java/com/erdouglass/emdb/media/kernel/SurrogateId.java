package com.erdouglass.emdb.media.kernel;

import java.util.Objects;

public record SurrogateId(Long value) {

  public SurrogateId {
    Objects.requireNonNull(value, "public id is required");
    if (value <= 0) throw new IllegalArgumentException("public id must be positive");
  }
  
  public static SurrogateId of(Long id) { return new SurrogateId(id); }
}
