package com.erdouglass.emdb.media;

import java.util.Objects;

public record Overview(String value) {
  public static final int MAX_LENGTH = 4_000;

  public Overview {
      Objects.requireNonNull(value, "overview is required");
      value = value.strip();
      if (value.length() > MAX_LENGTH) {
          throw new IllegalArgumentException(
                  "overview must be at most %d characters, was %d".formatted(MAX_LENGTH, value.length()));
      }
  }
  
  public static Overview of(String overview) {
    return new Overview(overview);
  }
}
