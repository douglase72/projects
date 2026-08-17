package com.erdouglass.emdb.media.kernel;

import java.text.Normalizer;
import java.util.Objects;

public record Overview(String value) implements ValueObject<String> {
  public static final int MAX_LENGTH = 1000;
  
  public Overview {
    Objects.requireNonNull(value, "overview is required");
    value = Normalizer.normalize(normalizeLineEndings(value), Normalizer.Form.NFC).strip();
    if (value.isBlank()) {
      throw new IllegalArgumentException("overview must not be blank");
    }
    if (value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException(
          "overview must not exceed %d characters".formatted(MAX_LENGTH));
    }
  }
  
  public static Overview of(String overview) {
    return new Overview(overview);
  } 
  
  private static String normalizeLineEndings(String value) {
    return value.replace("\r\n", "\n").replace('\r', '\n');
  }
}
