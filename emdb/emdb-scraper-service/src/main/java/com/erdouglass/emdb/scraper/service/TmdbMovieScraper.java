package com.erdouglass.emdb.scraper.service;

import java.time.LocalDate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.command.AuditMessage;
import com.erdouglass.emdb.common.command.AuditMetadata.EventSource;
import com.erdouglass.emdb.common.command.AuditMetadata.EventType;
import com.erdouglass.emdb.common.command.IngestMessage;
import com.erdouglass.emdb.common.command.MovieCreateMessage;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

/// Service responsible for scraping movie metadata from The Movie Database (TMDB).
///
/// This component acts as a **Consumer** for ingest commands and a **Producer** for 
/// both movie creation requests and audit trail updates. It bridges the gap between 
/// the external TMDB API and the internal `emdb-media-service`.
///
/// **Architecture Note:**
/// This service is designed to handle long-running operations. It runs on a worker 
/// thread (due to {@code @Blocking}) to prevent blocking the underlying 
/// reactive event loop.
@ApplicationScoped
public class TmdbMovieScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREATE_KEY = "movie.create";
  
  @Inject
  @Channel("movie-create-out") 
  Emitter<MovieCreateMessage> createEmitter;
  
  @Inject
  @Channel("audit-trail-out")
  Emitter<AuditMessage> auditEmitter;
  
  /// Ingest the TMDB movie identified in the given message.
  ///
  /// **Note**
  /// This method takes one message at a time from the movie-ingest-in queue and scrapes 
  /// TMDB for the relevant data including movie details, movie credits, and movie cast
  /// and crew. This can be a long running task that gets off loaded to a platform thread
  /// so that the event I/O thread does not get blocked.
  ///
  /// @param message The {@link AuditMessage} containing the execution context (Trace ID) 
  ///                and the specific TMDB ID to be scraped.
  @Blocking
  @Incoming("movie-ingest-in")
  public void onMessage(@NotNull @Valid IngestMessage message) {
    var tmdbId = message.tmdbId();
    var traceId = Span.current().getSpanContext().getTraceId();
    
    try {
      var msg = String.format("Ingest started for TMDB movie %d", tmdbId);
      updateProgress(traceId, EventType.STARTED, msg, 1, tmdbId);
      
      // Get the movie details from TMDB and update the progress.
      Thread.sleep(1000);
      msg = String.format("Fetched TMDB movie %d details", message.tmdbId());
      updateProgress(traceId, EventType.PROGRESS, msg, 33, tmdbId);
      
      // Get the movie cast and crew from TMDB and update the progress.
      Thread.sleep(3000);
      msg = String.format("Fetched %d people for TMDB movie %d", 359, message.tmdbId());
      updateProgress(traceId, EventType.PROGRESS, msg, 66, tmdbId);   
      
      // Put the create message in the queue for the media service to consume.
      var createMessage = MovieCreateMessage.builder()
          .tmdbId(818)
          .title("Austin Powers in Goldmember")
          .releaseDate(LocalDate.parse("2002-07-26"))
          .score(5.992f)
          .status(ShowStatus.RELEASED)
          .runtime(94)
          .budget(63000000)
          .revenue(296938801)
          .homepage("https://www.warnerbros.com/movies/austin-powers-goldmember")
          .originalLanguage("en")
          .backdrop("/kuPpElzfYnzsCye0hF8EbJSrvwo.jpg")
          .poster("/n8V61f1v7idya4WJzGEJNoIp9iL.jpg")
          .tagline("The grooviest movie of the summer has a secret, baby!")
          .overview("The world's most shagadelic spy continues his fight against Dr. Evil. This time, the diabolical doctor and his clone, Mini-Me, team up with a new foe—'70s kingpin Goldmember. While pursuing the team of villains to stop them from world domination, Austin gets help from his dad and an old girlfriend.")        
          .build();
      createEmitter.send(Message.of(createMessage)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(CREATE_KEY)
          .build()));
      msg = String.format("TMDB movie %d queued for persistence", message.tmdbId());
      updateProgress(traceId, EventType.PROGRESS, msg, 67, tmdbId);  
    } catch (Exception e) {
      var msg = String.format("Failed to scrape TMDB movie %d", tmdbId);
      updateProgress(traceId, EventType.FAILED, msg, 0, tmdbId);
      LOGGER.error(msg, e);
    }
  }
  
  /// Publishes an asynchronous status update to the `audit-trail-out` channel.
  ///
  /// **Note**
  /// By default, SmallRye Reactive Messaging buffers messages emitted during the execution 
  /// of an {@code @Incoming} method until that method completes. This behavior delays 
  /// UI progress bars.
  ///
  /// To bypass this buffering and send the status **immediately**, this method:
  /// 1. Captures the current {@link Context} (to preserve OpenTelemetry/MDC traces).
  /// 2. Spawns a **Virtual Thread**.
  /// 3. Executes the emit operation within that isolated thread, forcing an immediate flush 
  ///    to the broker.
  ///
  /// @param traceId  The OpenTelemetry trace ID for distributed tracing.
  /// @param type     The {@link EventType} (e.g., STARTED, PROGRESS, FAILED).
  /// @param message  A human-readable status description.
  /// @param complete The percentage of completion (0-100).
  /// @param tmdbId   The ID of the movie being processed.
  private void updateProgress(
      String traceId, EventType type, String message, Integer complete, Integer tmdbId) {
    try {
      var ctx = Context.current();
      Thread.ofVirtual().start(ctx.wrap(() -> {
        var updateMessage = AuditMessage.of(traceId, EventSource.SCRAPER, type, message, complete, tmdbId);
        auditEmitter.send(updateMessage);
      })).join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
