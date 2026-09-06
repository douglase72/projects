package com.erdouglass.emdb.media.kernel;

import java.util.Locale;
import java.util.Objects;

/// An ISO 639-1 two-letter language code.
///
/// Normalised to lower case on construction using [Locale#ROOT], not the default
/// locale — under a Turkish locale `"TR".toLowerCase()` yields a dotless `ı` and
/// would fail its own validation, which is the kind of defect that only appears
/// in one region's deployment.
///
/// Only the two-letter shape is enforced, not membership of the ISO register, so
/// a well-formed but unassigned code is accepted.
///
/// @param value the normalised code: exactly two lower-case ASCII letters
public record LanguageCode(String value) {
  public static final int LENGTH = 2;
    
  public LanguageCode {
    Objects.requireNonNull(value, "language code must not be null");
    value = value.toLowerCase(Locale.ROOT).strip();
    if (!value.matches("[a-z]{2}")) {
      throw new IllegalArgumentException("Invalid ISO 639-1 language code: " + value);
    }
  }
  
  public static LanguageCode of(String code) { return new LanguageCode(code); }
}
