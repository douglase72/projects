package com.erdouglass.emdb.media.kernel;

import java.util.Objects;

public record CastOrder(Integer value) {
  
  public CastOrder {
    Objects.requireNonNull(value, "cast order is required");
    if (value < 0) {
      throw new IllegalArgumentException("cast order must be positive or zero");
    }
  }
  
  public static CastOrder of(Integer order) {
    return new CastOrder(order);
  }
}
