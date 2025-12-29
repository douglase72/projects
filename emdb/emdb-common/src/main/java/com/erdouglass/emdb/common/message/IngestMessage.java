package com.erdouglass.emdb.common.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngestMessage(@NotNull @Positive Integer tmdbId) {
  
  public static IngestMessage of(Integer tmdbId) {
    return new IngestMessage(tmdbId);
  }

}
