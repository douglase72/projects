package com.erdouglass.emdb.scraper.annotation;

import java.time.Duration;
import java.time.Instant;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusContext;
import com.erdouglass.emdb.common.event.IngestStatusProducer;

@UpdateScraperStatus
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class UpdateScraperStatusInterceptor {
  private static final Logger LOGGER = Logger.getLogger(UpdateScraperStatusInterceptor.class);
  
  @Inject
  IngestStatusProducer producer;
  
  @Inject
  IngestStatusContext statusContext;
  
  @AroundInvoke
  Object update(InvocationContext context) throws Exception {
    var start = Instant.now();
    Object result = context.proceed();
    var et = Duration.between(start, Instant.now()).toMillis();
    var event = statusContext.get();
    if (event != null) {
      var msg = String.format("%s in %d ms", event.message(), et);
      LOGGER.info(msg);
      var id = event.id() != null ? event.id() : statusContext.getCorrelationId(); 
      producer.send(IngestStatusChanged.builder(event)
          .id(id)
          .message(msg)
          .build());      
    } else {
      LOGGER.errorf("@UpdateScraperStatus method %s.%s did not set a status event",
          context.getMethod().getDeclaringClass().getSimpleName(),
          context.getMethod().getName());      
    }
    return result;
  }
}
