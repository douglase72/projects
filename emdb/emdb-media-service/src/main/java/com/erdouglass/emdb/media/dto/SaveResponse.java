package com.erdouglass.emdb.media.dto;

import java.util.Objects;
import java.util.UUID;

public record SaveResponse(UUID id, String status) {

  public SaveResponse {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static SaveResponse of(UUID id, String status) { return new SaveResponse(id, status); }
}
