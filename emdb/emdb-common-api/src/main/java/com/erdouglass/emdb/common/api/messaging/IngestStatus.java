package com.erdouglass.emdb.common.api.messaging;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IngestStatus {
  SUBMITTED("Submitted"),
  STARTED("Started"),
  EXTRACTED("Extracted"),
  LOADED("Loaded"),
  COMPLETED("Completed"),
  FAILED("Failed");
  
  private final String status;
  
  IngestStatus(String status) {
    this.status = status;
  }
  
  @Override
  @JsonValue
  public String toString() {
    return status;
  }
}
