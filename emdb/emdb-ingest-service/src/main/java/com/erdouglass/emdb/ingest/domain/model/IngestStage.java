package com.erdouglass.emdb.ingest.domain.model;

public enum IngestStage {
  SUBMITTED("Submitted"),
  STARTED("Started"),
  EXTRACTED("Extratcted"),
  COMPLETED("Completed"),
  FAILED("Failed");
  
  private final String stage;
  
  IngestStage(String stage) {
    this.stage = stage;
  }
  
  @Override
  public String toString() {
    return stage;
  }  
}
