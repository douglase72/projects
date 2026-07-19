package com.erdouglass.emdb.media.domain.shared;

import com.erdouglass.emdb.media.MediaType;

public record PublicId(MediaType type, Long value) {
  
  public PublicId {
    if (type == null) {
      throw new IllegalArgumentException("media type must not be null");
    }
    if (value == null || value < 1) {
      throw new IllegalArgumentException("invalid id");
    }
  }
  
  public static PublicId of(MediaType type, Long value) {
    return new PublicId(type, value);
  }

  @Override
  public String toString() {
    return type.toString() + "_" + value;
  }
}
