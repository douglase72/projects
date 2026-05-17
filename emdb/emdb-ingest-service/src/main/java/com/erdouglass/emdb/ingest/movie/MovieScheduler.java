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
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// This class is responsible for scheduling the movie ingest process.
@ApplicationScoped
public class MovieScheduler {
  private final NoArgGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  
  @Inject
  @Channel("ingest-media-out") 
  Emitter<IngestMedia> emitter;
  
  /// Execute the movie scheduler on a virtual thread so the event loop doesn't 
  /// get blocked while fetching changes from TMDB. 
  @RunOnVirtualThread
  @Scheduled(cron = "{emdb.movie.scheduler}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
  public void execute() {
    var changes = getChanges();
    for (var tmdbId : changes) {
      var correlationId = GENERATOR.generate();
      var command = IngestMedia.of(tmdbId, MediaType.MOVIE, IngestSource.SCHEDULER);
      emitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
              .withCorrelationId(correlationId.toString())
              .withHeader(Configuration.START_TIME, Instant.now().toString())
              .withHeader(LoggingDecorator.EVENT_TYPE, command.getClass().getSimpleName())
              .build())); 
    }
  }
  
  /// Get the movies that have changed in the last 24 hours from TMDB. The 
  /// fetches need to be rate limited to avoid overwhelming the TMDB API with 
  /// concurrent requests.
  private List<Integer> getChanges() {
    return List.of(335984);
  }
}
