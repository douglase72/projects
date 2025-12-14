package com.erdouglass.emdb.media.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.AuditMessage;
import com.erdouglass.emdb.common.command.AuditMetadata.EventSource;
import com.erdouglass.emdb.common.command.AuditMetadata.EventType;
import com.erdouglass.emdb.common.command.MovieCreateMessage;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;

import io.opentelemetry.api.trace.Span;
import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  @Channel("audit-trail-out")
  Emitter<AuditMessage> emitter;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  MovieService service;
  
  @RunOnVirtualThread
  @Incoming("movie-create-in")
  public void onMessage(MovieCreateMessage message) {
    var tmdbId = message.tmdbId();
    var traceId = Span.current().getSpanContext().getTraceId();
    
    try {
      var msg = String.format("Persistence started for TMDB movie %d", tmdbId);
      updateProgress(traceId, EventType.PROGRESS, msg, 68, tmdbId);
      
      var movie = service.create(mapper.toMovie(message));
      msg = String.format("Persistence completed for TMDB movie %d", movie.tmdbId());
      updateProgress(traceId, EventType.COMPLETED, msg, 100, tmdbId);
    } catch (Exception e) {
      var msg = String.format("Failed to persist TMDB movie %d", traceId, tmdbId);
      updateProgress(traceId, EventType.FAILED, msg, 0, tmdbId);
      LOGGER.error(msg, e);
    }
  }
  
  private void updateProgress(
      String traceId, EventType type, String message, Integer complete, Integer tmdbId) {
    var updateMessage = AuditMessage.of(traceId, EventSource.MEDIA, type, message, complete, tmdbId);
    emitter.send(updateMessage);
  }

}
