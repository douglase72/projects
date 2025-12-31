package com.erdouglass.emdb.scheduler.service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.message.CronMessage;
import com.erdouglass.emdb.common.message.IngestMessage;

import io.opentelemetry.api.baggage.Baggage;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class TmdbMovieScheduler {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScheduler.class);
  
  @Inject
  @Channel("movie-ingest-out") 
  Emitter<IngestMessage> emitter;
  
  /// Periodically fetches and synchronizes movie data from TMDB.
  /// 
  /// Cron Examples:
  /// - @Scheduled(cron = "0 0 0 * * ?") - Run every day at midnight UTC (17:00:00 MST)
  /// - @Scheduled(cron = "0 0 8 * * ?") - Run every day at 08:00:00 UTC (01:00:00 MST)
  /// - @Scheduled(cron = "0 10 0 * * ?") - Run every day at 00:10:00 UTC
  @Scheduled(cron = "0 0 8 * * ?")
  public void cron() {
    var tmdbIds = List.of(816, 335984, 78);
    for (var tmdbId : tmdbIds) {
      ingest(tmdbId);
    }
  }
  
  @Incoming("movie-cron-in")
  public void executeNow(CronMessage message) {
    cron();
  }
  
  /// Initiates the ingest job for the given movie.
  ///
  /// This method initiates the movie ingest job by creating the {@link IngestMessage}
  /// for the given TMDB movie ID and putting it in the movie-ingest queue.for the
  /// emdb-scraper-service to consume.
  ///
  /// @param tmdbId the TMDB ID of the movie to ingest
  private void ingest(int tmdbId) {
    var jobId = UUID.randomUUID();
    var baggage = Baggage.current().toBuilder()
        .put("job-id", jobId.toString())
        .put("job-start-time", Instant.now().toString())
        .build();
    try (var _ = baggage.makeCurrent()) {
      var message = IngestMessage.of(tmdbId);
      emitter.send(Message.of(message)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(Configuration.INGEST_KEY)
          .build()));       
      LOGGER.infof("Sent: %s for TMDB movie %d", jobId, tmdbId);
    }
  }

}
