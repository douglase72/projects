package com.erdouglass.emdb.common.api.logging;

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
public class LogInterceptor {
  private static final Logger LOGGER = Logger.getLogger(LogInterceptor.class);
  
  @AroundInvoke
  public Object log(InvocationContext context) throws Exception {
    var start = Instant.now();
    var result = context.proceed();
    var duration = Duration.between(start, Instant.now());
    var annotation = context.getMethod().getAnnotation(Log.class);
    if (annotation == null) {
        annotation = context.getMethod().getDeclaringClass().getAnnotation(Log.class);
    }
    var action = annotation != null ? annotation.value() : "Executed:";
    LOGGER.infof("%s %s in %d ms", action, result, duration.toMillis());
    return result;
  }
}
