package com.erdouglass.emdb.media.kernel;

import java.util.Objects;

public record PublicId(Long value) {

  public PublicId {
    Objects.requireNonNull(value, "public id is required");
    if (value <= 0) throw new IllegalArgumentException("public id must be positive");
  }
  
  public static PublicId of(Long id) { return new PublicId(id); }
}
