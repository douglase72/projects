package com.erdouglass.emdb.media.logging;

import java.time.Duration;
import java.time.Instant;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.jboss.logging.Logger;

@Log
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
class LogInterceptor {
  private static final Logger LOGGER = Logger.getLogger(LogInterceptor.class);
  
  @AroundInvoke
  Object log(final InvocationContext context) throws Exception {
    var method = context.getMethod();
    var start = Instant.now();
    var result = context.proceed();
    var et = Duration.between(start, Instant.now()).toMillis();
    LOGGER.infof("%s %s in %d ms", action(method.getName()), result, et);
    return result;
  }
  
  private String action(final String method) {
    return switch (method) {
      case "save" -> "Saved:";
      default -> throw new IllegalArgumentException("Invalid method: " + method);
    };
  }
}
