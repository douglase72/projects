package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.util.Objects;
import java.util.UUID;

public record SaveMovieResponse(UUID id, String status) { 
  
  public SaveMovieResponse {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static SaveMovieResponse of(UUID id, String status) {
    return new SaveMovieResponse(id, status);
  }
}
