package com.erdouglass.emdb.ingest.domain.model;

public enum IngestStatus {
  SUBMITTED("Submitted"),
  STARTED("Started"),
  EXTRACTED("Extratcted"),
  LOADED("Loaded"),
  COMPLETED("Completed"),
  FAILED("Failed");
  
  private final String status;
  
  IngestStatus(String status) {
    this.status = status;
  }
  
  @Override
  public String toString() {
    return status;
  }  
}
