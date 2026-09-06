package com.erdouglass.emdb.media.person.domain.model;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Gender {
  FEMALE("Female"),
  MALE("Male"),
  NON_BINARY("Non-Binary");
  
  public static final int MAX_LENGTH  = 10;
  
  private static final Map<String, Gender> BY_LABEL = Arrays.stream(values())
      .collect(Collectors.toUnmodifiableMap(g -> normalize(g.value), Function.identity()));
  
  private final String value;
  
  Gender(String value) {
    this.value = value;
  }
  
  public static Gender from(String gender) {
    if (gender == null || gender.isBlank()) throw new IllegalArgumentException("invalid gender");
    var result = BY_LABEL.get(normalize(gender));
    if (result == null) throw new IllegalArgumentException("invalid gender");
    return result;
  }
  
  public String value() {
    return value;
  }
  
  @Override
  public String toString() {
    return value;
  }
  
  /// Reduces a label to its comparable form: lower case, letters and digits only.
  ///
  /// [Locale#ROOT] rather than the default locale, so the result does not depend
  /// on where the application runs — under a Turkish locale the default would
  /// lower-case `I` to a dotless `ı` and the lookup would miss.
  ///
  /// @param value the text to normalise, never `null`
  /// @return the normalised key
  private static String normalize(String value) {
    return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
  }
}
