package com.erdouglass.emdb.media.domain.shared;

import java.util.Objects;

public record Overview(String value) {
  public static final int MAX_LENGTH = 4000;
  
  public Overview {
    Objects.requireNonNull(value, "overview is required");
    if (value.isBlank() || value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException("overview must be between 1 and %d".formatted(MAX_LENGTH));
    }
  }
  
  public static Overview of(String overview) {
    return new Overview(overview);
  } 
}
