package com.erdouglass.emdb.media;

/// Outcome of [SaveMovieUseCase#save]: which movie, which snapshot (the
/// *post-write* version, ready to echo into a subsequent edit), and whether
/// the write created or updated.
public record SaveResult(String id, Long version, Status status) {

  public enum Status {
    CREATED,
    UPDATED;
  }  
}
