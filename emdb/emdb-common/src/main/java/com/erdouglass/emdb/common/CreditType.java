package com.erdouglass.emdb.common;

public enum CreditType {
  CAST("cast"), 
  CREW("crew");
  
  private final String type;
  
  CreditType(String type) {
    this.type = type;
  }
  
  @Override
  public String toString() {
    return type;
  }
  
}
