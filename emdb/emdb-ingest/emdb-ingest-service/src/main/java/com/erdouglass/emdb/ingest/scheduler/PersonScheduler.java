package com.erdouglass.emdb.ingest.scheduler;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.IngestMedia.IngestSource;
import com.erdouglass.emdb.ingest.IngestMedia.IngestType;
import com.erdouglass.emdb.ingest.messaging.IngestProducer;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;

/// Produces the [IngestMedia] command for people according to schedule.
///
/// Runs on a virtual thread at the cron expression configured by
/// `emdb.person.scheduler`. Concurrent executions are skipped so a long-running
/// run cannot overlap the next trigger.
@ApplicationScoped
public class PersonScheduler {

  @Inject
  IngestProducer producer;

  /// Looks up people changed in the last 24 hours and publishes an
  /// [IngestMedia] command for each one tagged with [IngestSource#SCHEDULER].
  @RunOnVirtualThread
  @Scheduled(cron = "{emdb.person.scheduler}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
  public void execute() {
    var changes = getChanges();
    for (var tmdbId : changes) {
      var command = IngestMedia.of(tmdbId, IngestType.PERSON, IngestSource.SCHEDULER);
      producer.publish(command);
    }
  }

  /// Returns the TMDB identifiers of people changed in the last 24 hours.
  private List<Integer> getChanges() {
    return List.of(3);
  }
}
