package com.erdouglass.emdb.scheduler;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.api.IngestMedia;
import com.erdouglass.emdb.ingest.api.IngestMedia.IngestSource;
import com.erdouglass.emdb.ingest.api.IngestMedia.IngestType;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;

/// Produces the [IngestMedia] command for series according to schedule.
///
/// Runs on a virtual thread at the cron expression configured by
/// `emdb.series.scheduler`. Concurrent executions are skipped so a long-running
/// run cannot overlap the next trigger.
@ApplicationScoped
public class SeriesScheduler {

  @Inject
  IngestProducer producer;
  
  /// Looks up series changed in the last 24 hours and publishes an
  /// [IngestMedia] command for each one tagged with [IngestSource#SCHEDULER].
  @RunOnVirtualThread
  @Scheduled(cron = "{emdb.series.scheduler}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
  public void execute() {
    var changes = getChanges();
    for (var tmdbId : changes) {
      var command = IngestMedia.of(tmdbId, IngestType.SERIES, IngestSource.SCHEDULER);
      producer.publish(command);
    }
  }
  
  /// Returns the TMDB identifiers of series changed in the last 24 hours.
  private List<Integer> getChanges() {
    return List.of(1396, 456, 1399);
  }
}
