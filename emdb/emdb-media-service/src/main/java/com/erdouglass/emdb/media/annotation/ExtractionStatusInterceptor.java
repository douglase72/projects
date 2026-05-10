package com.erdouglass.emdb.media.annotation;

import java.time.Duration;
import java.time.Instant;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import com.erdouglass.emdb.common.api.MediaType;
import com.erdouglass.emdb.media.api.command.SaveCommand;
import com.erdouglass.emdb.media.api.command.SaveMovie;
import com.erdouglass.emdb.media.api.command.SavePerson;
import com.erdouglass.emdb.media.api.command.SaveSeries;
import com.erdouglass.emdb.messaging.api.IngestSource;
import com.erdouglass.emdb.messaging.api.IngestStatus;
import com.erdouglass.emdb.messaging.api.IngestStatusChanged;
import com.erdouglass.emdb.messaging.api.IngestStatusEmitter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@ExtractionStatus
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class ExtractionStatusInterceptor {
  
  @Inject 
  IngestContext context;
  
  @Inject
  IngestStatusEmitter emitter;
  
  @Inject
  MeterRegistry registry;  

  @AroundInvoke
  Object send(InvocationContext context) throws Exception {
    var start = Instant.now();
    var result = context.proceed();
    var et = Duration.between(start, Instant.now());
    
    if (result instanceof SaveCommand command) {
      switch (command) {
        case SaveMovie cmd -> send(cmd.tmdbId(), MediaType.MOVIE, cmd.title(), et);
        case SavePerson cmd -> send(cmd.tmdbId(), MediaType.PERSON, cmd.name(), et);
        case SaveSeries cmd -> send(cmd.tmdbId(), MediaType.SERIES, cmd.title(), et);
      }
    }
    return result;
  }
  
  private void send(int tmdbId, MediaType type, String name, Duration et) {
    emitter.send(IngestStatusChanged.builder()
      .id(context.getCorrelationId())
      .tmdbId(tmdbId)
      .status(IngestStatus.EXTRACTED)
      .source(IngestSource.SCRAPER)
      .type(type)
      .name(name)
      .message(String.format("Ingest of TMDB %d %s fetched from TMDB in %d ms", tmdbId, type, et.toMillis()))
      .build());
    Timer.builder("emdb.scrape.duration")
      .description("Measures the time spent scraping the media from TMDB")
      .tag("media", type.toString())
      .register(registry)
      .record(et);
  }
}
