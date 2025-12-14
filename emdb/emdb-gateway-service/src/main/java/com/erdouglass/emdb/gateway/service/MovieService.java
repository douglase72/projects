package com.erdouglass.emdb.gateway.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

import io.opentelemetry.api.trace.Span;

@ApplicationScoped
public class MovieService {
  private static final Logger LOGGER = Logger.getLogger(MovieService.class);
  
  public String ingest(@NotNull @Positive Integer tmdbId) {
    var jobId = Span.current().getSpanContext().getTraceId();
    LOGGER.infof("jobId: %s", jobId);
    return jobId;
  }

}
