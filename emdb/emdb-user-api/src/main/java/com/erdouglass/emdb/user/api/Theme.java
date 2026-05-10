package com.erdouglass.emdb.user.api;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Theme {
  DARK("dark"),
  LIGHT("light");
  
  private static final Map<String, Theme> CACHE = Stream.of(values())
      .collect(Collectors.toMap(Theme::name, Function.identity()));
  
  private final String type;
  
  Theme(String type) {
    this.type = type;
  }
  
  @JsonCreator
  public static Theme from(String type) {
    return Optional.ofNullable(CACHE.get(type.toUpperCase()))
        .orElseThrow(() -> new IllegalArgumentException("Invalid theme: " + type));
  }  

  @Override
  @JsonValue
  public String toString() {
    return type;
  }
}
