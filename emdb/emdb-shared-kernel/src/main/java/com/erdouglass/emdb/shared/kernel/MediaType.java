package com.erdouglass.emdb.shared.kernel;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MediaType {
  MOVIE("movie"), 
  PERSON("person"),
  SERIES("series");
  
  private static final Map<String, MediaType> LOOKUP = Stream.of(values())
      .collect(Collectors.toMap(Object::toString, Function.identity()));
  
  private final String value;
  
  MediaType(String value) {
    this.value = value;
  }
  
  public static MediaType from(String type) {
    Objects.requireNonNull(type, "type is required");
    var result = LOOKUP.get(type.toLowerCase(Locale.ROOT).trim());
    if (result == null) {
      throw new IllegalArgumentException("invalid media type: " + type);
    }
    return result;
  }
  
  @Override
  public String toString() {
    return value;
  }
}
