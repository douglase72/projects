package com.erdouglass.emdb.ingest.api;

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
  
  private final String mediaType;
  
  IngestType(String mediaType) {
    this.mediaType = mediaType;
  }
  
  public static IngestType from(String mediaType) {
    Objects.requireNonNull(mediaType, "type is required");
    var result = LOOKUP.get(mediaType.toLowerCase().trim());
    if (result == null) {
      throw new IllegalArgumentException("invalid mediaType: " + mediaType);
    }
    return result;
  }
  
  @Override
  public String toString() {
    return mediaType;
  }
}
