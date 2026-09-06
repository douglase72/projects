package com.erdouglass.emdb.media.person.domain.model;

import java.text.Normalizer;
import java.util.Objects;

/// A person's biography: free-text prose describing their life and career.
///
/// Normalised on construction so that cosmetically identical text is literally
/// identical. Three things are levelled, in order:
///
/// * line endings — `\r\n` and bare `\r` both become `\n`
/// * Unicode form — converted to NFC
/// * surrounding whitespace — stripped
///
/// All three exist for the same reason: a change to a biography is detected by
/// comparing the stored text with the incoming text, so any difference that does
/// not render is a difference that writes a spurious audit row. A feed that
/// switches line endings, or emits a combining accent where it previously
/// emitted a precomposed character, would otherwise register an update on every
/// title in the catalogue.
///
/// Internal formatting is otherwise preserved. Paragraph breaks and indentation
/// are part of the prose and are left alone.
///
/// Blank text is rejected rather than stored, so "no biography" is expressed by
/// the absence of the value rather than by an empty string — which keeps the
/// audit trail honest: clearing a biography is a removal, not an update to
/// whitespace.
///
/// Because normalisation happens inside the compact constructor, [#value()]
/// returns the normalised form, not the text that was passed in.
///
/// @param value the normalised biography: non-blank, NFC, `\n` line endings, at
///        most [#MAX_LENGTH] characters
public record Biography(String value) {
  
  /// The maximum length of a normalised biography, in `char` units. Also used to
  /// size the database column, so the same limit applies at every layer.
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
