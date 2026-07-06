package com.erdouglass.emdb.media;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
  UNKNOWN("Unknown"),
  FEMALE("Female"),
  MALE("Male"),
  NON_BINARY("Non-Binary");
    
  private static final Map<String, Gender> GENDER_CACHE = Stream.of(values())
      .collect(Collectors.toMap(Gender::toString, Function.identity()));
  
  private final String gender;
     
  Gender(String gender) {
    this.gender = gender;
  }
  
  @JsonCreator
  public static Gender from(String gender) {
    var match = GENDER_CACHE.get(gender);
    return match != null ? match : Gender.valueOf(gender);
  }
  
  @Override
  @JsonValue
  public String toString() {
    return gender;
  }
}
