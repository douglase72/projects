package com.erdouglass.emdb.scraper.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.message.AuditMessage;
import com.erdouglass.emdb.common.message.AuditMessage.MessageSource;
import com.erdouglass.emdb.common.message.AuditMessage.MessageType;
import com.erdouglass.emdb.common.message.IngestMessage;
import com.erdouglass.emdb.common.message.MovieCreateMessage;

import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class TmdbMovieScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREATE_KEY = "movie.create";
  
  @Inject
  @Channel("audit-log-out")
  Emitter<AuditMessage> auditEmitter;
  
  @Inject
  @Channel("movie-create-out") 
  Emitter<MovieCreateMessage> createEmitter;
  
  @Blocking
  @Incoming("movie-ingest-in")
  public void onMessage(IngestMessage message) {
    var jobId = message.jobId();
    var tmdbId = message.tmdbId();
    var latency = Duration.between(message.timestamp(), Instant.now()).toMillis();
    LOGGER.infof("Received: %s, latency: %d ms", message, latency);
    
    try {
      var msg = String.format("Ingest started for TMDB movie %d", tmdbId);
      updateProgress(jobId, MessageType.STARTED, msg, 1);
      
      Thread.sleep(1000);
      msg = String.format("Fetched TMDB movie %d details", message.tmdbId());
      updateProgress(jobId, MessageType.PROGRESS, msg, 30);
      
      Thread.sleep(3000);
      msg = String.format("Fetched %d people for TMDB movie %d", 359, message.tmdbId());
      updateProgress(jobId, MessageType.PROGRESS, msg, 70);
      
      // Send create movie message to media service
      msg = String.format("TMDB movie %d queued for persistence", message.tmdbId());
      updateProgress(jobId, MessageType.PROGRESS, msg, 71);
      var createMessage = MovieCreateMessage.builder()
          .jobId(jobId)
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
      var msg = String.format("Failed to scrape TMDB movie %d", tmdbId);
      updateProgress(jobId, MessageType.FAILED, msg, 0);
      LOGGER.error(msg, e);
    }
  }
  
  private void updateProgress(
      String jobId, MessageType type, String message, Integer progress) {
    var auditMessage = AuditMessage.of(jobId, MessageSource.SCRAPER, type, message, progress);
    auditEmitter.send(Message.of(auditMessage).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(Configuration.AUDIT_KEY)
        .build()));
    LOGGER.infof("Sent: %s", auditMessage);    
  }

}
