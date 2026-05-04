package com.erdouglass.emdb.common.api.messaging;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IngestSource {
  GATEWAY("emdb-gateway"),
  MEDIA("emdb-media"),
  SCHEDULER("emdb-scheduler"),
  SCRAPER("emdb-scraper");
  
  private final String source;
  
  IngestSource(String source) {
    this.source = source;
  }
  
  @Override
  @JsonValue
  public String toString() {
    return source;
  }
}
