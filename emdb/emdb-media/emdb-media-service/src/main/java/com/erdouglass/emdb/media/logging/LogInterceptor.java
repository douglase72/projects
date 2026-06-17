package com.erdouglass.emdb.media.logging;

import java.time.Duration;
import java.time.Instant;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.query.OffsetPage;

@Log
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
class LogInterceptor {
  private static final Logger LOGGER = Logger.getLogger(LogInterceptor.class);
  
  @AroundInvoke
  Object log(final InvocationContext context) throws Exception {
    var annotation = context.getMethod().getAnnotation(Log.class);
    var action = annotation.value();
    var start = Instant.now();
    var result = context.proceed();
    var et = Duration.between(start, Instant.now()).toMillis();
    switch (result) {
      case OffsetPage<?> p -> LOGGER.infof("%s %d results in %d ms", action, p.size(), et);
      default -> LOGGER.infof("%s %s in %d ms", action, result, et);
    }
    return result;
  }
}
