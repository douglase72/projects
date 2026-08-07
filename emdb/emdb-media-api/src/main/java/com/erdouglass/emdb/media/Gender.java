package com.erdouglass.emdb.media;

public enum Gender {
  UNKNOWN("Unknown"),
  FEMALE("Female"),
  MALE("Male"),
  NON_BINARY("Non-Binary");
  
  private final String gender;
     
  Gender(String gender) {
    this.gender = gender;
  }
  
  @Override
  public String toString() {
    return gender;
  }
}
