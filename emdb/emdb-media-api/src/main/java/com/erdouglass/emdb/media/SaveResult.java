package com.erdouglass.emdb.media;

import jakarta.validation.constraints.NotNull;

public record SaveResult(@NotNull String id, @NotNull Status status) {

  public enum Status {
    CREATED,
    UPDATED;
  }  
}
