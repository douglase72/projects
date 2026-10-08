package com.erdouglass.emdb.ingest.domain.model;

public enum IngestStatus {
  SUBMITTED("Submitted"),
  STARTED("Started"),
  EXTRACTED("Extratcted"),
  COMPLETED("Completed"),
  FAILED("Failed");
  
  private final String value;
  
  IngestStatus(String value) {
    this.value = value;
  }
  
  @Override
  public String toString() {
    return value;
  }  
}
