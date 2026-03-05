package com.erdouglass.emdb.common;

import com.fasterxml.jackson.annotation.JsonValue;

/// Represents the core classifications of media supported by the EMDB system.
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
