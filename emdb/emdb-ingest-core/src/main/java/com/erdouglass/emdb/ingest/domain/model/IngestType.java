package com.erdouglass.emdb.ingest.domain.model;

public enum IngestType {
  MOVIE("movie"), 
  PERSON("person"),
  SERIES("series");
  
  private final String type;
  
  IngestType(String type) {
    this.type = type;
  }
  
  @Override
  public String toString() {
    return type;
  }
}
