package com.erdouglass.emdb.media.kernel;

import java.text.Normalizer;
import java.util.Objects;

/// The display title of a show, normalised on construction.
///
/// Two normalisations happen before validation, and both matter for equality:
/// surrounding whitespace is stripped, and the text is converted to Unicode NFC.
/// Without NFC, a title typed with a combining accent and the same title typed
/// with a precomposed character are different strings — they render identically,
/// compare unequal, and would produce a spurious change in the audit trail on
/// every ingestion run.
///
/// Because normalisation happens inside the compact constructor, [#value()]
/// returns the normalised form, not the text that was passed in.
///
/// Rejects text that is blank once stripped, and text longer than
/// [#MAX_LENGTH] after normalisation.
///
/// @param value the normalised title: non-blank, NFC, at most [#MAX_LENGTH]
///        characters
public record Title(String value) implements ValueObject<String> {
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
