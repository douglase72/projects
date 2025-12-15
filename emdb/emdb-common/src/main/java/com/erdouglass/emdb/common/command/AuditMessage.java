package com.erdouglass.emdb.common.command;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuditMessage(
    @NotBlank String jobId,
    @NotNull Instant timestamp,
    @NotBlank String message) {
  
  public static AuditMessage of(String jobId, String message) {
    return new AuditMessage(jobId, Instant.now(), message);
  }

}
