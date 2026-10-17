package com.erdouglass.emdb.media.shared.application;

import java.util.Objects;

import com.erdouglass.emdb.media.shared.domain.model.MediaId;

public record SaveResult(MediaId id, Status status) {

  public SaveResult {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static SaveResult of(MediaId id, Status status) {
    return new SaveResult(id, status);
  }
  
  public enum Status {
    CREATED,
    UPDATED,
    UNCHANGED;
  }
}
