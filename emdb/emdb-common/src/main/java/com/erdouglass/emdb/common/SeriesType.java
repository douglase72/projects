package com.erdouglass.emdb.common;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SeriesType {
  SCRIPTED("Scripted"),
  REALITY("Reality"),
  DOCUMENTARY("Documentary"),
  NEWS("News"),
  TALK_SHOW("Talk Show"),
  MINISERIES("Miniseries"),
  VIDEO("Video");
  
  private static final Map<String, SeriesType> CACHE = Stream.of(values())
      .collect(Collectors.toMap(SeriesType::name, Function.identity()));
  
  private final String type;
  
  SeriesType(String type) {
    this.type = type;
  }
  
  @JsonCreator
  public static SeriesType from(String type) {
    return Optional.ofNullable(CACHE.get(type.replace(" ", "_").toUpperCase()))
        .orElseThrow(() -> new IllegalArgumentException("Invalid series type: " + type));
  }
  
  @Override
  @JsonValue
  public String toString() {
    return type;
  }
}
