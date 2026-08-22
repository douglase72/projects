package com.erdouglass.emdb.media.movie.domain;

import java.util.Objects;
import java.util.UUID;

public record CreditId(UUID value) {

  public CreditId {
    Objects.requireNonNull(value, "credit id must not be null");
  }
  
  public static CreditId of(UUID value) {
    return new CreditId(value);
  }
}
