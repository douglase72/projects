package com.erdouglass.emdb.media.api;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
  UNKNOWN("Unknown", 0),
  FEMALE("Female", 1),
  MALE("Male", 2),
  NON_BINARY("Non-Binary", 3);
    
  private static final Map<String, Gender> GENDER_CACHE = Stream.of(values())
      .collect(Collectors.toMap(Gender::name, Function.identity()));
  private static final Map<Integer, Gender> ID_CACHE = Stream.of(values())
      .collect(Collectors.toMap(Gender::id, Function.identity()));
  
  private final String gender;
  private final Integer id;
     
  Gender(String gender, int id) {
    this.gender = gender;
    this.id = id;
  }
  
  public static Gender from(Integer id) {
    return Optional.ofNullable(ID_CACHE.get(id))
        .orElseThrow(() -> new IllegalArgumentException("Invalid id: " + id));
  }
  
  @JsonCreator
  public static Gender from(String gender) {
    return Optional.ofNullable(GENDER_CACHE.get(gender.replace("-", "_").toUpperCase()))
        .orElseThrow(() -> new IllegalArgumentException("Invalid gender: " + gender));
  }
  
  public Integer id() {
    return id;
  }
  
  @Override
  @JsonValue
  public String toString() {
    return gender;
  }
}
