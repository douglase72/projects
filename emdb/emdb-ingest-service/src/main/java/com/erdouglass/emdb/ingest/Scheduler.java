package com.erdouglass.emdb.ingest;

/// Strategy for an ingest scheduler that emits commands to fetch and persist
/// media from an external source.
///
/// Implementations live in their media-type-specific sub-packages (e.g.,
/// [com.erdouglass.emdb.ingest.movie.MovieScheduler]) and are typically
/// driven by a cron trigger, with on-demand invocation available through
/// [IngestResource]. Dispatch from generic callers should be keyed by
/// [MediaType] — for example, by injecting `Map<MediaType, Scheduler>`.
public interface Scheduler {
  
  /// Runs one pass of the scheduler, emitting an [IngestMedia] command for
  /// each piece of media that should be re-ingested.
  ///
  /// Implementations are responsible for their own concurrency control
  /// (e.g., skipping overlapping runs) and for any rate limiting required
  /// by the upstream source.  
  void execute();
}
