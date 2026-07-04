package com.erdouglass.emdb.ingest.logging;

import java.time.Duration;
import java.time.Instant;

import jakarta.annotation.Priority;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.IngestCompleted;
import com.erdouglass.emdb.ingest.IngestEvent;
import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.IngestProgressed;
import com.erdouglass.emdb.ingest.IngestStarted;
import com.erdouglass.emdb.ingest.core.IngestHandler;
import com.erdouglass.emdb.ingest.core.IngestUtilities;
import com.erdouglass.emdb.ingest.core.Scraper;

@Log
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
class LogInterceptor {
  private static final Logger LOGGER = Logger.getLogger(LogInterceptor.class);
  
  @Inject
  Event<IngestEvent> emitter;
  
  @AroundInvoke
  Object log(InvocationContext context) throws Exception {
    Object result = null;
    var parameters = context.getParameters();
    if (parameters.length > 0
        && parameters[0] instanceof Message<?> message
        && message.getPayload() instanceof IngestMedia payload) {
      var target = context.getTarget();
      if (target instanceof IngestHandler) {
        var start = IngestUtilities.ingestStart(message);
        var et = Duration.between(start, Instant.now()).toMillis();
        var type = payload.type();
        var tmdbId = payload.tmdbId();
        var msg = String.format("Ingest of TMDB %s %d sat in the 'ingest-media' queue for %d ms", type, tmdbId, et);
        LOGGER.infof(msg);
        emitter.fire(new IngestStarted(IngestUtilities.correlationId(message), msg, type));
        result = context.proceed();
        et = Duration.between(start, Instant.now()).toMillis();
        msg = String.format("Ingest of TMDB %s %d completed in %d ms", type, tmdbId, et);
        LOGGER.infof(msg);
        emitter.fire(new IngestCompleted(IngestUtilities.correlationId(message), msg, type));        
      } else if (target instanceof Scraper) {
        var start = Instant.now();
        result = context.proceed();
        var et = Duration.between(start, Instant.now()).toMillis();
        var type = payload.type();
        var msg = String.format("Extracted TMDB %s %d in %d ms", type, payload.tmdbId(), et);
        LOGGER.infof(msg);
        emitter.fire(new IngestProgressed(IngestUtilities.correlationId(message), msg, type));
      }
    }
    return result;
  }
}
