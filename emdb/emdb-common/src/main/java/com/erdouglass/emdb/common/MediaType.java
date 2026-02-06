package com.erdouglass.emdb.common;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaType {
  MOVIE("movie"), 
  PERSON("person"),
  SERIES("series");
  
  private final String type;
  
  MediaType(String type) {
    this.type = type;
  }
  
  @Override
  @JsonValue
  public String toString() {
    return type;
  }
}
