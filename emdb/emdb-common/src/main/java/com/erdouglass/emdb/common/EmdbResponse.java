package com.erdouglass.emdb.common;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.core.Response;

public record EmdbResponse(
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotNull Integer status) {

  public static EmdbResponse of(Long id, Integer tmdbId, Response.Status status) {
    return new EmdbResponse(id, tmdbId, status.getStatusCode());
  }
}
