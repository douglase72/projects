package com.erdouglass.emdb.media.show;

import java.util.Map;
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
      .collect(Collectors.toMap(SeriesType::toString, Function.identity()));
  
  private final String type;
  
  SeriesType(String type) {
    this.type = type;
  }
  
  @JsonCreator
  public static SeriesType from(String type) {
    var match = CACHE.get(type);
    return match != null ? match : SeriesType.valueOf(type);
  }
  
  @Override
  @JsonValue
  public String toString() {
    return type;
  }
}
