package com.erdouglass.emdb.media.movie.domain.model;

import java.text.Normalizer;
import java.util.Objects;

public record Role(String value) {
  public static final int MAX_LENGTH = 250;
  
  public Role {
    Objects.requireNonNull(value, "role must not be null");
    value = Normalizer.normalize(value.strip(), Normalizer.Form.NFC);
    if (value.isBlank()) {
      throw new IllegalArgumentException("role must not be blank");
    }
  }
  
  public static Role of(String role) {
    return new Role(role);
  }
}
