package com.erdouglass.emdb.common.query;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CreditType {
  CAST("cast"), 
  CREW("crew");
  
  private static final Map<String, CreditType> CACHE = Stream.of(values())
      .collect(Collectors.toMap(CreditType::name, Function.identity()));
  
  private final String type;
  
  CreditType(String type) {
    this.type = type;
  }
  
  @JsonCreator
  public static CreditType from(String type) {
    return Optional.ofNullable(CACHE.get(type.toUpperCase()))
        .orElseThrow(() -> new IllegalArgumentException("Invalid credit type: " + type));
  }
   
  @Override
  @JsonValue
  public String toString() {
    return type;
  }
  
}
