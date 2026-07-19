package com.erdouglass.emdb.media.domain.shared;

import java.util.Objects;
import java.util.UUID;

public record CreditId(UUID value) {

  public CreditId {
    Objects.requireNonNull(value, "credit id must not be null");
  }
  
  public static CreditId of(UUID id) {
    return new CreditId(id);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
