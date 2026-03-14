package com.erdouglass.emdb.common.query;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MultiResponseDto(
    @NotNull Integer statusCode,
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId) {

  public static MultiResponseDto of(Integer status, Long id, Integer tmdbId) {
    return new MultiResponseDto(status, id, tmdbId);
  }
  
}
