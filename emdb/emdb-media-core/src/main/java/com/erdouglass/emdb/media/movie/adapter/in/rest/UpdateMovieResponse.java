package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.util.Objects;
import java.util.UUID;

public record UpdateMovieResponse(UUID id, Long version, String status) {

  public UpdateMovieResponse {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static UpdateMovieResponse of(UUID id, Long version, String status) {
    return new UpdateMovieResponse(id, version, status);
  }
}
