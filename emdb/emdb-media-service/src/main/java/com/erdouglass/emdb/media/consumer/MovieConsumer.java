package com.erdouglass.emdb.media.consumer;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.AuditMessage;
import com.erdouglass.emdb.common.command.AuditMetadata.EventSource;
import com.erdouglass.emdb.common.command.AuditMetadata.EventType;
import com.erdouglass.emdb.common.command.MovieCreateMessage;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  private static final String UPDATE_KEY = "audit.update";
  
  @Inject
  @Channel("audit-trail-out")
  Emitter<AuditMessage> auditEmitter;
  
  @RunOnVirtualThread
  @Incoming("movie-create-in")
  public void onMessage(MovieCreateMessage message) {
    var meta = message.meta();
    var tmdbId = message.tmdbId();
    var traceId = message.meta().traceId();
    
    try {
      var msg = String.format("Persistence started for TMDB movie %d", tmdbId);
      var lag = Duration.between(meta.timestamp(), Instant.now()).toMillis();
      updateProgress(traceId, EventType.STARTED, msg, 68, lag, tmdbId);
      
      Thread.sleep(1000);
      msg = String.format("Persistence completed for TMDB movie %d", tmdbId);
      updateProgress(traceId, EventType.COMPLETED, msg, 100, tmdbId);
    } catch (Exception e) {
      updateProgress(traceId, EventType.FAILED, e.getMessage(), 0, tmdbId);
      var msg = String.format("[%s] Failed to persist TMDB movie %d", traceId, tmdbId);
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
    var updateMessage = AuditMessage.of(traceId, EventSource.MEDIA, type, message, complete, lag, tmdbId);
    auditEmitter.send(Message.of(updateMessage).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withRoutingKey(UPDATE_KEY)
        .build()));
  }

}
