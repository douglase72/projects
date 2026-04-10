package com.erdouglass.emdb.media.dto;
import jakarta.validation.constraints.NotNull;

public record SaveResult<T>(@NotNull SaveStatus status, @NotNull T entity) {

  public enum SaveStatus {
    CREATED,
    UPDATED,
    UNCHANGED;
  }
  
  public static <T> SaveResult<T> of(SaveStatus status, T entity) {
    return new SaveResult<>(status, entity);
  }
  
}
