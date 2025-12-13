package com.erdouglass.emdb.scraper.service;

import java.time.Duration;
import java.time.Instant;
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
import com.erdouglass.emdb.common.command.MovieCreateMessage;

import io.opentelemetry.context.Context;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class TmdbMovieScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREATE_KEY = "movie.create";
  private static final String UPDATE_KEY = "audit.update";
  
  @Inject
  @Channel("movie-create-out") 
  Emitter<MovieCreateMessage> createEmitter;
  
  @Inject
  @Channel("audit-trail-out")
  Emitter<AuditMessage> auditEmitter;
  
  /// Ingest the TMDB movie identified in the given message.
  ///
  /// This method takes one message at a time from the movie-ingest-in queue and scrapes 
  /// TMDB for the relevant data including movie details, movie credits, and movie cast
  /// and crew. This can be a long running task that gets off loaded to a platform thread
  /// so that the event I/O thread does not get blocked.
  ///
  /// @param message the {@link AuditMessage} containing the TMDB ID of the movie to ingest.
  @Blocking
  @Incoming("movie-ingest-in")
  public void onMessage(@NotNull @Valid AuditMessage message) {
    LOGGER.infof("Received: %s", message);
    var meta = message.meta();
    var tmdbId = message.tmdbId();
    var traceId = message.meta().traceId();
    
    try {
      var msg = String.format("Ingest started for TMDB movie %d", tmdbId);
      var lag = Duration.between(meta.timestamp(), Instant.now()).toMillis();
      updateProgress(traceId, EventType.STARTED, msg, 1, lag, tmdbId);
      
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
          .traceId(meta.traceId())
          .source(EventSource.SCRAPER)
          .type(EventType.SUBMITTED)
          .message(String.format("TMDB movie %d queued for persistence", message.tmdbId()))
          .percentComplete(67)
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
      LOGGER.infof("Sent: %s", createMessage);
    } catch (Exception e) {
      updateProgress(traceId, EventType.FAILED, e.getMessage(), 0, tmdbId);
      var msg = String.format("[%s] Failed to scrape TMDB movie %d", traceId, tmdbId);
      LOGGER.error(msg, e);
    }
  }
  
  private void updateProgress(
      String traceId, 
      EventType type, 
      String message, 
      Integer complete, 
      Integer tmdbId) {
    updateProgress(traceId, type, message, complete, null, tmdbId);
  }
  
  private void updateProgress(
      String traceId, 
      EventType type, 
      String message, 
      Integer complete, 
      Long lag, 
      Integer tmdbId) {
    var ctx = Context.current();
    Thread.ofVirtual().start(ctx.wrap(() -> {
      var updateMessage = AuditMessage.of(traceId, EventSource.SCRAPER, type, message, complete, lag, tmdbId);
      auditEmitter.send(Message.of(updateMessage)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(UPDATE_KEY)
          .build()));      
    }));    
  }

}
