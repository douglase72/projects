package com.erdouglass.emdb.common.query;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovieStatus(
    @NotNull @Positive Integer tmdbId,
    @Positive Long emdbId,
    @NotNull MessageType type,
    @NotNull MessageStatus status) {
  
  public enum MessageStatus {
    QUEUED,
    RECEIVED,
    EXTRACTED,
    LOADED,
    COMPLETED;
  }

  public enum MessageType {
    INGEST,
    SYNCHRONIZE;
  }
  
  public static MovieStatus of(Integer tmdbId, MessageType type, MessageStatus status) {
    return new MovieStatus(tmdbId, null, type, status);
  }
  
  public static MovieStatus of(Long emdbId, Integer tmdbId, MessageType type, MessageStatus status) {
    return new MovieStatus(tmdbId, emdbId, type, status);
  }
  
}
