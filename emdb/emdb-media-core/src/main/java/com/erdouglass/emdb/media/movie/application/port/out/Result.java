package com.erdouglass.emdb.media.movie.application.port.out;

/// What a write actually did, and the state the caller should hold afterwards.
///
/// The returned version is the value to send back on the next update or lock
/// call. It is the version *after* the write, so a caller can chain writes
/// without re-reading — including after [Status#UNCHANGED], where the version is
/// unmoved but still current.
///
/// @param id the public catalogue id of the affected title, e.g. `mv_42`
/// @param version the optimistic-locking version as of the end of the call
/// @param status which outcome occurred
public record Result(String id, Long version, Status status) {
  
  public static Result of(String id, Long version, Status status) {
    return new Result(id, version, status);
  }

  /// The three outcomes a write can have.
  ///
  /// Callers translating this to HTTP typically map [#CREATED] to `201` and the
  /// other two to `200`; [#UNCHANGED] is reported rather than hidden so that
  /// clients can skip cache invalidation and downstream notification.
  public enum Status {
    CREATED,
    UPDATED,
    UNCHANGED;
  }  
}
