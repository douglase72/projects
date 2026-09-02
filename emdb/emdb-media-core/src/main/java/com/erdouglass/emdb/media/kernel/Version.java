package com.erdouglass.emdb.media.kernel;

import java.util.Objects;

/// An optimistic-locking version, incremented by the persistence layer on every
/// write.
///
/// Clients receive the version with each read and must send it back on the next
/// write; a mismatch means someone else wrote in between and the client's edit
/// is refused rather than allowed to overwrite silently.
///
/// Zero is valid and is the version a row carries when first inserted.
///
/// @param value the version, never `null` and never negative
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
}
