package com.erdouglass.emdb.ingest.movie;

import java.time.Instant;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.LoggingDecorator;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.IngestMedia.IngestSource;
import com.erdouglass.emdb.ingest.MediaType;
import com.erdouglass.emdb.ingest.Scheduler;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// [Scheduler] that queries TMDB for recently-changed movies and emits an
/// [IngestMedia] command for each one to the ingest-media queue.
///
/// Each emitted command gets a freshly-generated UUIDv7 correlation ID so
/// downstream consumers (and dead-letter handlers) can trace a single
/// movie's journey through the pipeline.
@ApplicationScoped
class MovieScheduler implements Scheduler {
  private final NoArgGenerator generator = Generators.timeBasedEpochGenerator();
  
  @Inject
  @Channel("ingest-media-out") 
  Emitter<IngestMedia> emitter;
  
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
      var correlationId = generator.generate();
      var command = IngestMedia.of(tmdbId, MediaType.MOVIE, IngestSource.SCHEDULER);
      emitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
              .withCorrelationId(correlationId.toString())
              .withHeader(Configuration.START_TIME, Instant.now().toString())
              .withHeader(LoggingDecorator.EVENT_TYPE, command.getClass().getSimpleName())
              .build())); 
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
    return List.of(335984);
  }
}
