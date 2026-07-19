package com.erdouglass.emdb.media;

public enum MediaType {
  MOVIE("mv"), 
  PERSON("pr"),
  SERIES("sr");
  
  private final String type;
  
  MediaType(String type) {
    this.type = type;
  }
  
  @Override
  public String toString() {
    return type;
  }
}
