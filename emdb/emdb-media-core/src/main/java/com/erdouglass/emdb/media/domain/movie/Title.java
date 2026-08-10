package com.erdouglass.emdb.media.domain.movie;

import java.text.Normalizer;
import java.util.Objects;

public record Title(String value) {
  public static final int MAX_LENGTH = 140;
  
  public Title {
    Objects.requireNonNull(value, "title must not be null");
    value = Normalizer.normalize(value.strip(), Normalizer.Form.NFC);
    if (value.isBlank()) {
      throw new IllegalArgumentException("title must not be blank");
    }
    if (value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException(
          "title must not exceed %d characters".formatted(MAX_LENGTH));
    }
  }
  
  public static Title of(String title) {
    return new Title(title);
  }
}
