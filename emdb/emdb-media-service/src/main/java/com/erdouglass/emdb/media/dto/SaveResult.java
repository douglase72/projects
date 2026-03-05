package com.erdouglass.emdb.media.dto;

public record SaveResult<T>(Status status, T entity) {
  
  public enum Status {
    CREATED,
    UPDATED;
  }
  
  public static <T> SaveResult<T> of(Status status, T entity) {
    return new SaveResult<>(status, entity);
  }

}
