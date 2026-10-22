package com.erdouglass.emdb.media.shared.application;

import java.util.Objects;

import com.erdouglass.emdb.media.shared.domain.model.MediaId;
import com.erdouglass.emdb.media.shared.domain.model.Version;

public record UpdateResult(MediaId id, Version version, Status status) {

  public UpdateResult {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static UpdateResult of(MediaId id, Version version, Status status) {
    return new UpdateResult(id, version, status);
  }
  
  public enum Status {
    UPDATED,
    UNCHANGED;
  }
}
