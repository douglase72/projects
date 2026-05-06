package com.erdouglass.emdb.media.annotation;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.common.api.command.IngestMedia;
import com.erdouglass.emdb.common.api.messaging.IngestSource;
import com.erdouglass.emdb.common.api.messaging.IngestStatus;
import com.erdouglass.emdb.common.api.messaging.IngestStatusChanged;
import com.erdouglass.emdb.common.api.messaging.IngestStatusEmitter;
import com.erdouglass.emdb.media.utils.MessageMetadata;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;

@UpdateStatus
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class UpdateStatusInterceptor {
  
  @Inject
  IngestContext ingestContext;
  
  @Inject 
  IngestStatusEmitter emitter;
  
  @Inject
  MeterRegistry registry;  
  
  @AroundInvoke
  Object update(InvocationContext context) throws Exception {
    var message = getMessage(context);
    var command = message.getPayload();
    var correlationId = MessageMetadata.getCorrelationId(message);
    var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
        .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
    var start = Instant.parse(metadata.getHeaders().get(IngestMedia.START_TIME).toString());
    
    try {
      started(correlationId, command, start);
      var event = (IngestStatusChanged) context.proceed();
      emitter.send(event);
      Timer.builder("emdb.persist.duration")
        .description("Measures the time spent persisting media")
        .tag("media", event.type().toString())
        .register(registry)
        .record(ingestContext.getPersistDuration());
      completed(event, start);
      return event;
    } catch (Exception e) {
      emitter.send(IngestStatusChanged.builder()
          .id(correlationId)
          .tmdbId(command.tmdbId())
          .status(IngestStatus.FAILED)
          .source(IngestSource.MEDIA)
          .type(command.type())
          .message(String.format("Ingest of TMDB %s %d failed", command.type(), command.tmdbId()))
          .build(), e);
      throw e;
    }
  }
  
  private void started(UUID correlationId, IngestMedia command, Instant start) {
    var et = Duration.between(start, Instant.now());
    var msg = String.format("Ingest of TMDB %s %d sat in the queue for %d ms", 
        command.type(), command.tmdbId(), et.toMillis());
    emitter.send(IngestStatusChanged.builder()
        .id(correlationId)
        .tmdbId(command.tmdbId())
        .status(IngestStatus.STARTED)
        .source(IngestSource.MEDIA)
        .type(command.type())
        .message(msg)
        .build());
    Timer.builder("emdb.ingest.queue.duration")
      .description("Measures the time spent in the queue")
      .tag("media", command.type().toString())
      .register(registry)
      .record(et);
  }
  
  private void completed(IngestStatusChanged event, Instant start) {
    var et = Duration.between(start, Instant.now());
    var msg = String.format("Ingest of TMDB %s %d completed in %d ms", event.type(), event.tmdbId(), et.toMillis());
    emitter.send(IngestStatusChanged.builder(event)
        .status(IngestStatus.COMPLETED)
        .message(msg)
        .build());
    Timer.builder("emdb.ingest.duration")
      .description("Measures the time ingesting the media from TMDB")
      .tag("media", event.type().toString())
      .register(registry)
      .record(et);
  }
  
  @SuppressWarnings("unchecked")
  private Message<IngestMedia> getMessage(InvocationContext ctx) {
    for (var p : ctx.getParameters()) {
      if (p instanceof Message<?> m) {
        return (Message<IngestMedia>) m;
      }
    }
    throw new IllegalStateException("No Message parameter on " + ctx.getMethod());
  }
}
