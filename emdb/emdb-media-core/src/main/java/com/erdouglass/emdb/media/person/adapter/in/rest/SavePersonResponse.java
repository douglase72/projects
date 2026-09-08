package com.erdouglass.emdb.media.person.adapter.in.rest;

import java.util.Objects;
import java.util.UUID;

public record SavePersonResponse(UUID id, String status) {
  
  public SavePersonResponse {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static SavePersonResponse of(UUID id, String status) {
    return new SavePersonResponse(id, status);
  }
}
