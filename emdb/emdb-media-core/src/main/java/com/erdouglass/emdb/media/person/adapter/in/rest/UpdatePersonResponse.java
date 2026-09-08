package com.erdouglass.emdb.media.person.adapter.in.rest;

import java.util.Objects;
import java.util.UUID;

public record UpdatePersonResponse(UUID id, Long version, String status) {
  
  public UpdatePersonResponse {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static UpdatePersonResponse of(UUID id, Long version, String status) {
    return new UpdatePersonResponse(id, version, status);
  }
}
