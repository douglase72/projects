package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.util.Objects;
import java.util.UUID;

public record MovieResponse(UUID id, Long version, String status) { 
  
  public MovieResponse {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static MovieResponse of(UUID id, Long version, String status) {
    return new MovieResponse(id, version, status);
  }
}
