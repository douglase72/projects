package com.erdouglass.emdb.common.message;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CronMessage(
    @NotNull UUID id,
    @NotNull Instant timestamp) {
  
  public static CronMessage of(UUID id) {
    return new CronMessage(id, Instant.now());
  }

}
