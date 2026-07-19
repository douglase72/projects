package com.erdouglass.emdb.media.domain.shared;

import java.util.Locale;
import java.util.Objects;

public record OriginalLanguage(String code) {

  public OriginalLanguage {
    Objects.requireNonNull(code, "language code must not be null");
    code = code.toLowerCase(Locale.ROOT).strip();
    if (!code.matches("[a-z]{2}")) {
      throw new IllegalArgumentException("Invalid ISO 639-1 language code: " + code);
    }
  }
  
  public static OriginalLanguage of(String code) {
    return new OriginalLanguage(code);
  }
  
  @Override
  public String toString() {
    return code;
  }
}
