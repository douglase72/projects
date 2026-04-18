package com.erdouglass.emdb.media.annotation;

import java.time.Duration;
import java.time.Instant;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusProducer;

@SendStatus
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class SendStatusInterceptor {
  private static final Logger LOGGER = Logger.getLogger(SendStatusInterceptor.class);
  
  @Inject
  IngestStatusProducer producer;
  
  @Inject
  IngestStatusContext statusContext;
  
  @AroundInvoke
  public Object sendStatus(InvocationContext context) throws Exception {    
    var start = Instant.now();
    Object result = context.proceed();
    var event = statusContext.get();
    if (event != null) {
      var et = Duration.between(start, Instant.now()).toMillis();
      var msg = String.format("Ingest job for TMDB %s %d completed in %d ms", event.type(), event.tmdbId(), et);
      LOGGER.info(msg);
      producer.send(IngestStatusChanged.builder(event).message(msg).build());
    } else {
      LOGGER.warnf("@SendStatus method %s.%s did not set a status event",
          context.getMethod().getDeclaringClass().getSimpleName(),
          context.getMethod().getName());      
    }
    return result;
  }
}
