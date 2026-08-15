package com.erdouglass.emdb.media;

public record SaveResult(String id, Long version, Status status) {
  
  public static SaveResult of(String id, Long version, Status status) {
    return new SaveResult(id, version, status);
  }

  public enum Status {
    CREATED,
    UPDATED,
    UNCHANGED;
  }  
}
