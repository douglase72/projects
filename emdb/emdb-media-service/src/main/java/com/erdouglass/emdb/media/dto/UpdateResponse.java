package com.erdouglass.emdb.media.dto;

import java.util.Objects;
import java.util.UUID;

public record UpdateResponse(UUID id, Long version, String status) {

  public UpdateResponse {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static UpdateResponse of(UUID id, Long version, String status) {
    return new UpdateResponse(id, version, status);
  }  
}
