package com.erdouglass.emdb.audit.service;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.AuditMetadata.EventSource;
import com.erdouglass.emdb.common.command.AuditMetadata.EventType;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class AuditService {
  private static final Logger LOGGER = Logger.getLogger(AuditService.class);
  private static final String LATENCY = "latency";
  private static final String META = "meta";
  private static final String MESSAGE = "message";
  private static final String PERCENT_COMPLETE = "percentComplete";
  private static final String SOURCE = "source";
  private static final String TIMESTAMP = "timestamp";
  private static final String TMDB_ID = "tmdbId";
  private static final String TRACE_ID = "traceId";
  private static final String TYPE = "type";
  
  @RunOnVirtualThread
  @Incoming("audit-trail-in")
  public void onMessage(JsonObject message) {
    var meta = message.getJsonObject(META);
    var traceId = meta.getString(TRACE_ID);
    var timestamp = meta.getInstant(TIMESTAMP);
    var source = EventSource.valueOf(meta.getString(SOURCE));
    var type = EventType.valueOf(meta.getString(TYPE));
    var msg = meta.getString(MESSAGE);
    var complete = meta.getInteger(PERCENT_COMPLETE);
    var lag = meta.getLong(LATENCY);
    var tmdbId = message.getInteger(TMDB_ID);
    var log = String.format("TMDB ID: %d, Source: %s, Type: %s, Message: %s, Complete: %d%%", 
        tmdbId, source, type, msg, complete);
    if (type == EventType.STARTED) {
      log = String.format("TMDB ID: %d, Source: %s, Type: %s, Message: %s, Complete: %d%%, Latency: %d ms", 
          tmdbId, source, type, msg, complete, lag);
    }
    LOGGER.info(log);
  }
  
}
