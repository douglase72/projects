package com.erdouglass.emdb.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/// Outcome of [SaveMovieUseCase#save]: which movie, which snapshot (the
/// *post-write* version, ready to echo into a subsequent edit), and whether
/// the write created or updated.
public record SaveResult(
    @NotBlank String id,
    @PositiveOrZero Long version,
    @NotNull Status status) {

  public enum Status {
    CREATED,
    UPDATED;
  }  
}
