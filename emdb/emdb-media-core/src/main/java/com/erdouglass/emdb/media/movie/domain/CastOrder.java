package com.erdouglass.emdb.media.movie.domain;

import java.util.Objects;

public record CastOrder(Integer value) {

  public CastOrder {
    Objects.requireNonNull(value, "billing order is required");
    if (value < 0) {
      throw new IllegalArgumentException("billing order must be positive or zero");
    }
  }
  
  public static CastOrder of(Integer order) {
    return new CastOrder(order);
  }
}
