package com.erdouglass.emdb.media.person.domain.model;

import java.text.Normalizer;
import java.util.Objects;

public record Biography(String value) {
  public static final int MAX_LENGTH = 4000;
  
  public Biography {
    Objects.requireNonNull(value, "biography must not be null");
    value = Normalizer.normalize(normalizeLineEndings(value), Normalizer.Form.NFC).strip();
    if (value.isBlank()) {
      throw new IllegalArgumentException("biography must not be blank");
    }
    if (value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException(
          "biography must not exceed %d characters".formatted(MAX_LENGTH));
    }
  }
  
  public static Biography of(String biography) {
    return new Biography(biography);
  }
  
  private static String normalizeLineEndings(String value) {
    return value.replace("\r\n", "\n").replace('\r', '\n');
  }
}
