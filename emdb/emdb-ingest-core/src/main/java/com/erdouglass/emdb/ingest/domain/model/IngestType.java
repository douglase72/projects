package com.erdouglass.emdb.ingest.domain.model;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum IngestType {
  MOVIE("movie"), 
  PERSON("person"),
  SERIES("series");
  
  private static final Map<String, IngestType> LOOKUP = Stream.of(values())
      .collect(Collectors.toMap(Object::toString, Function.identity()));
  
  private final String type;
  
  IngestType(String type) {
    this.type = type;
  }
  
  public static IngestType from(String type) {
    Objects.requireNonNull(type, "type is required");
    var result = LOOKUP.get(type.toLowerCase().trim());
    if (result == null) {
      throw new IllegalArgumentException("invalid type: " + type);
    }
    return result;
  }
  
  @Override
  public String toString() {
    return type;
  }
}
