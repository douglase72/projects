package com.erdouglass.emdb.media.logging;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.dto.SaveResult;

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
      LogDuration annotation = context.getMethod().getAnnotation(LogDuration.class);
      String action = annotation != null ? annotation.value() : "Executed:";
      Object logSubject;
      if (result instanceof Optional<?> opt) {
        logSubject = opt.isPresent() ? opt.get() : "Nothing";
      } else if (result instanceof SaveResult<?> sr) {
        logSubject = sr.entity();
      } else if (result == null) {
        logSubject = "";
      } else {
        logSubject = result;
      }
      LOGGER.infof("%s %s in %d ms", action, logSubject, et);
    }
  }
}
