package com.erdouglass.emdb.media.movie.domain;

import java.util.Objects;

public record BillingOrder(Integer value) {

  public BillingOrder {
    Objects.requireNonNull(value, "billing order is required");
    if (value < 0) {
      throw new IllegalArgumentException("billing order must be positive or zero");
    }
  }
  
  public static BillingOrder of(Integer order) {
    return new BillingOrder(order);
  }
}
