package com.erdouglass.emdb.ingest.domain.model;

public enum IngestStage {
  SUBMITTED("Submitted"),
  STARTED("Started"),
  EXTRACTED("Extratcted"),
  COMPLETED("Completed"),
  FAILED("Failed");
  
  private final String status;
  
  IngestStage(String status) {
    this.status = status;
  }
  
  @Override
  public String toString() {
    return status;
  }  
}
