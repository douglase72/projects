package com.erdouglass.emdb.media.kernel;

import java.util.Objects;

public record Version(Long value) {
  
  public Version {
    Objects.requireNonNull(value, "version must not be null");
    if (value < 0) {
      throw new IllegalArgumentException("version must be positive or zero");
    }
  }
  
  public static Version of(Long version) { return new Version(version); }
}
