package com.erdouglass.emdb.media.kernel;

import java.util.Objects;

public record Result(AggregateId id, Version version, Status status) {
  
  public Result {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static Result of(AggregateId id, Version version, Status status) {
    return new Result(id, version, status);
  }

  public enum Status {
    CREATED,
    UPDATED,
    UNCHANGED;
  }  
}
