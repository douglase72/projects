package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.domain.model.MovieId;

public record Result(MovieId id, Version version, Status status) {
  
  public Result {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static Result of(MovieId id, Version version, Status status) {
    return new Result(id, version, status);
  }

  public enum Status {
    CREATED,
    UPDATED,
    UNCHANGED;
  }  
}
