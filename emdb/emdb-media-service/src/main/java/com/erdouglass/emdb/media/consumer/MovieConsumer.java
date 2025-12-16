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

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.message.AuditMessage;
import com.erdouglass.emdb.common.message.MovieCreateMessage;
import com.erdouglass.emdb.common.message.AuditMessage.MessageSource;
import com.erdouglass.emdb.common.message.AuditMessage.MessageType;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  @Channel("audit-log-out")
  Emitter<AuditMessage> auditEmitter;
  
  @RunOnVirtualThread
  @Incoming("movie-create-in")
  public void onMessage(MovieCreateMessage message) {
    var jobId = message.jobId();
    var tmdbId = message.tmdbId();
    var latency = Duration.between(message.timestamp(), Instant.now()).toMillis();
    LOGGER.infof("Received: %s, latency: %d ms", message, latency);
    
    try {
      var msg = String.format("Persistence started for TMDB movie %d", tmdbId);
      updateProgress(jobId, MessageType.PROGRESS, msg, 72); 
      
      Thread.sleep(1000);
      msg = String.format("Persistence completed for TMDB movie %d", tmdbId);
      updateProgress(jobId, MessageType.COMPLETED, msg, 100);      
    } catch (Exception e) {
      var msg = String.format("Failed to persist TMDB movie %d", tmdbId);
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
