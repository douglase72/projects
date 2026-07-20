package com.erdouglass.emdb.media.domain.shared;

import java.util.Objects;

/// Optimistic-concurrency snapshot number of an aggregate. Opaque outside
/// the persistence machinery: compared for equality, never computed —
/// nothing in the domain may derive "next".
public record Version(Long value) {

  public Version {
    Objects.requireNonNull(value, "version must not be null");
    if (value < 0) {
      throw new IllegalArgumentException("Invalid version");
    }
  }
  
  public static Version of(Long version) {
    return new Version(version);
  }
  
  @Override
  public String toString() {
    return value.toString();
  }
}
