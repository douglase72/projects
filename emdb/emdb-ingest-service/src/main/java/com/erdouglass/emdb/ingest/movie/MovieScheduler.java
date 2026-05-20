package com.erdouglass.emdb.ingest.movie;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.IngestMedia.IngestSource;
import com.erdouglass.emdb.ingest.IngestProducer;
import com.erdouglass.emdb.ingest.MediaType;
import com.erdouglass.emdb.ingest.Scheduler;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;

/// [Scheduler] implementation for TMDB movies.
///
/// Periodically polls TMDB for movies that have changed in the last 24
/// hours and emits an [IngestMedia] command for each one, tagged with
/// [IngestSource#SCHEDULER] so downstream consumers can distinguish
/// scheduled refreshes from user-initiated ingests.
///
/// The cron expression is read from the `emdb.movie.scheduler` config
/// property so that the cadence can be tuned per environment without a
/// rebuild.
@ApplicationScoped
class MovieScheduler implements Scheduler {
  
  @Inject
  IngestProducer producer;
  
  /// {@inheritDoc}
  ///
  /// Executed on a virtual thread so the blocking TMDB calls inside
  /// [#getChanges] do not occupy a Vert.x event-loop thread. Overlapping
  /// runs are suppressed by `ConcurrentExecution.SKIP`, so a slow pass
  /// will not pile up on top of the next cron tick.
  @Override
  @RunOnVirtualThread
  @Scheduled(cron = "{emdb.movie.scheduler}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
  public void execute() {
    var changes = getChanges();
    for (var tmdbId : changes) {
      var command = IngestMedia.of(tmdbId, MediaType.MOVIE, IngestSource.SCHEDULER);
      producer.send(command);
    }
  }
  
  /// Returns the TMDB identifiers of movies changed in the last 24 hours.
  ///
  /// **Stub implementation.** Currently returns a single hard-coded ID for
  /// development. The real implementation will page through the TMDB
  /// `/movie/changes` endpoint with rate limiting to stay within TMDB's
  /// concurrent-request budget.
  ///
  /// @return the TMDB identifiers of changed movies
  private List<Integer> getChanges() {
    return List.of(816, 817);
  }
}
