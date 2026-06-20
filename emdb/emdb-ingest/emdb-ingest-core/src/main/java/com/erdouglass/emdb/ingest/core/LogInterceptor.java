package com.erdouglass.emdb.ingest.core;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.core.movie.MovieIngestConsumer;
import com.erdouglass.emdb.media.command.SaveMovie;

import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;

@Log
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
class LogInterceptor {
  private static final Logger LOGGER = Logger.getLogger(LogInterceptor.class);
  
  @AroundInvoke
  Object log(InvocationContext context) throws Exception {
    var target = context.getTarget();
    if (target instanceof MovieIngestConsumer) {
      var parameters = context.getParameters();
      if (parameters.length > 0
          && parameters[0] instanceof Message<?> message
          && message.getPayload() instanceof IngestMedia payload) {
        var start = getIngestStart(message);
        var et = Duration.between(start, Instant.now()).toMillis();
        var type = payload.type();
        var tmdbId = payload.tmdbId();
        LOGGER.infof("Ingest of TMDB %s %d sat in the 'ingest-media' queue for %d ms", type, tmdbId, et);
        var result = context.proceed();
        et = Duration.between(start, Instant.now()).toMillis();
        LOGGER.infof("Ingest of TMDB %s %d completed in %d ms", type, tmdbId, et);
        return result;
      }
    }
    var start = Instant.now();
    var result = context.proceed();
    var et = Duration.between(start, Instant.now()).toMillis();
    switch (result) {
      case SaveMovie m -> LOGGER.infof("Extracted TMDB movie %d in %d ms", m.tmdbId(), et);
      default -> LOGGER.infof("Extracted %s in %d ms", result, et);
    }
    return result;
  }
  
  private Instant getIngestStart(Message<?> message) {
    var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
        .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
    return Optional.ofNullable(metadata.getHeaders())
        .map(h -> Instant.parse(h.get(IngestMedia.START_TIME).toString()))
        .orElseThrow(() -> new IllegalArgumentException("Invalid ingest start time"));
  }
}
