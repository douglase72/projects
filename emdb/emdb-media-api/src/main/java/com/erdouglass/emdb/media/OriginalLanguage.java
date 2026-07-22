package com.erdouglass.emdb.media;

import java.util.Locale;
import java.util.Objects;

/// ISO 639-1 language code, canonicalized to lowercase on construction —
/// `"EN"`, `" en "`, and `"en"` are one value inside the hexagon.
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
