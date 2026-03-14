package com.erdouglass.emdb.media.dto;
import jakarta.validation.constraints.NotNull;

public record SaveResult<T>(@NotNull Status status, @NotNull T entity) {

  public enum Status {
    CREATED,
    UPDATED,
    UNCHANGED;
  }
  
  public static <T> SaveResult<T> of(Status status, T entity) {
    return new SaveResult<>(status, entity);
  }
  
}
