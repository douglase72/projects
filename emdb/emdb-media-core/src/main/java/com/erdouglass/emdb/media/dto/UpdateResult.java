package com.erdouglass.emdb.media.dto;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Version;

public record UpdateResult(PublicId id, Version version, Status status) {

  public UpdateResult {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static UpdateResult of(PublicId id, Version version, Status status) {
    return new UpdateResult(id, version, status);
  }
  
  public enum Status {
    UPDATED,
    UNCHANGED;
  }
}
