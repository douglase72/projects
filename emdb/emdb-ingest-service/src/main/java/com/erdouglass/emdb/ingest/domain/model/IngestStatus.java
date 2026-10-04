package com.erdouglass.emdb.ingest.domain.model;

public enum IngestStatus {
  SUBMITTED("Submitted"),
  STARTED("Started"),
  EXTRACTED("Extratcted"),
  LOADED("Loaded"),
  COMPLETED("Completed"),
  FAILED("Failed");
  
  private final String stage;
  
  IngestStatus(String stage) {
    this.stage = stage;
  }
  
  @Override
  public String toString() {
    return stage;
  }  
}