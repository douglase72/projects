package com.erdouglass.emdb.media.kernel;

import java.text.Normalizer;
import java.util.Objects;

public record Name(String value) {
  public static final int MAX_LENGTH = 80;
  
  public Name {
    Objects.requireNonNull(value, "name must not be null");
    value = Normalizer.normalize(value.strip(), Normalizer.Form.NFC);
    if (value.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
    if (value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException(
          "name must not exceed %d characters".formatted(MAX_LENGTH));
    }
  }
  
  public static Name of(String name) {
    return new Name(name);
  }
}
