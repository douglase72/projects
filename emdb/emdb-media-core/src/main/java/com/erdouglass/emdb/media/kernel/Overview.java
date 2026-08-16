package com.erdouglass.emdb.media.kernel;

import java.util.Objects;

/// A free-text synopsis.
///
/// A blank overview is rejected rather than stored, so "no overview" is
/// expressed by the absence of the value rather than by an empty string. That
/// keeps the audit trail meaningful: clearing an overview is a removal, not an
/// update to whitespace.
///
/// Unlike [Title], the text is stored as supplied — no stripping, no Unicode
/// normalisation — because a synopsis is prose whose internal formatting is
/// worth preserving.
///
/// @param value the synopsis: non-blank, at most [#MAX_LENGTH] characters
public record Overview(String value) implements ValueObject<String> {
  public static final int MAX_LENGTH = 4000;
  
  public Overview {
    Objects.requireNonNull(value, "overview is required");
    if (value.isBlank() || value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException("overview must be between 1 and %d".formatted(MAX_LENGTH));
    }
  }
  
  public static Overview of(String overview) {
    return new Overview(overview);
  } 
}
