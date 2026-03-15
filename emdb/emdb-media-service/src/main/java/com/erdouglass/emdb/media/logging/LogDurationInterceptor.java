package com.erdouglass.emdb.media.logging;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.jboss.logging.Logger;

@LogDuration
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LogDurationInterceptor {
  private static final Logger LOGGER = Logger.getLogger(LogDurationInterceptor.class);

  @AroundInvoke
  public Object logMethodExecutionTime(InvocationContext context) throws Exception {
    var start = Instant.now();
    Object result = null;

    try {
      result = context.proceed();
      return result;
    } finally {
      var et = Duration.between(start, Instant.now()).toMillis();
      var annotation = context.getMethod().getAnnotation(LogDuration.class);
      var action = annotation != null ? annotation.value() : "Executed:";
      Object subject;
      if (result instanceof Optional<?> opt) {
        subject = opt.isPresent() ? opt.get() : "Nothing";
      } else if (result instanceof Collection<?> col) {
        subject = col.size() + " ";
        if (annotation != null && !annotation.subject().isBlank()) {
          subject += annotation.subject();
        } else {
          subject += "entities";
        }
      } else if (result == null) {
        subject = "";
      } else {
        subject = result;
      }
      LOGGER.infof("%s %s in %d ms", action, subject, et);
    }
  }
}
