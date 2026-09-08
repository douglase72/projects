package com.erdouglass.emdb.media.dto;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.PublicId;

public record SaveResult(PublicId id, Status status) {

  public SaveResult {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static SaveResult of(PublicId id, Status status) {
    return new SaveResult(id, status);
  }
  
  public enum Status {
    CREATED,
    UPDATED,
    UNCHANGED;
  }
}
