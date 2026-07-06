package com.erdouglass.emdb.media;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SaveResult(
    @NotNull @Positive Long id,
    @NotNull Status status) {

  public enum Status {
    CREATED,
    UPDATED;
  }
}
