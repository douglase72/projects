package com.erdouglass.emdb.media.person.domain;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Gender {
  UNKNOWN("Unknown", 0),
  FEMALE("Female", 1),
  MALE("Male", 2),
  NON_BINARY("Non-Binary", 3);
  
  public static final int MAX_LENGTH  = 10;
  
  private static final Map<Integer, Gender> BY_ID = Arrays.stream(values())
      .collect(Collectors.toUnmodifiableMap(Gender::id, Function.identity()));  
  private static final Map<String, Gender> BY_LABEL = Arrays.stream(values())
      .collect(Collectors.toUnmodifiableMap(g -> normalize(g.value), Function.identity()));
  
  private final String value;
  private final Integer id;
  
  Gender(String value, int id) {
    this.value = value;
    this.id = id;
  }
  
  public static Optional<Gender> from(Integer id) {
    return id == null ? Optional.empty() : Optional.ofNullable(BY_ID.get(id));
  }
  
  public static Optional<Gender> from(String gender) {
    if (gender == null || gender.isBlank()) {
      return Optional.empty();
    }
    return Optional.ofNullable(BY_LABEL.get(normalize(gender)));
  }
  
  public Integer id() {
    return id;
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
