package com.erdouglass.emdb.common.command;

import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.common.command.AuditMetadata.EventSource;
import com.erdouglass.emdb.common.command.AuditMetadata.EventType;

public record AuditMessage(
    @NotNull @Valid AuditMetadata meta,
    @NotNull @Positive Integer tmdbId) {
  
  public static AuditMessage of(
      String traceId, 
      EventSource source, 
      EventType type,
      String message, 
      Integer percentComplete,
      Integer tmdbId) {
    return new AuditMessage(
        new AuditMetadata(traceId, Instant.now(), source, type, message, percentComplete, null), tmdbId);
  }
  
  public static AuditMessage of(
      String traceId, 
      EventSource source, 
      EventType type,
      String message, 
      Integer percentComplete,
      Long latency,
      Integer tmdbId) {
    return new AuditMessage(
        new AuditMetadata(traceId, Instant.now(), source, type, message, percentComplete, latency), tmdbId);
  }

}
