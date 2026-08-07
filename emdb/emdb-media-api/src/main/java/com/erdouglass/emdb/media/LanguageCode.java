package com.erdouglass.emdb.media;

import java.util.Locale;
import java.util.Objects;

public record LanguageCode(String value) {
  public static final int LENGTH = 2;
    
  public LanguageCode {
    Objects.requireNonNull(value, "language code must not be null");
    value = value.toLowerCase(Locale.ROOT).strip();
    if (!value.matches("[a-z]{2}")) {
      throw new IllegalArgumentException("Invalid ISO 639-1 language code: " + value);
    }
  }
  
  public static LanguageCode of(String code) {
    return new LanguageCode(code);
  }
}
