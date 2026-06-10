package com.erdouglass.emdb.media.person;

public enum MediaType {
  MOVIE("movie"), 
  PERSON("person"),
  SERIES("series");
  
  private final String type;
  
  MediaType(String type) {
    this.type = type;
  }
  
  @Override
  public String toString() {
    return type;
  }
}
