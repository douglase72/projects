package com.erdouglass.emdb.media.annotation;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.data.page.Page;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.event.IngestStatusProducer;

@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggedInterceptor {
  private static final Logger LOGGER = Logger.getLogger(LoggedInterceptor.class);
  private static final String SPACE = " ";
  
  @Inject 
  IngestStatusProducer producer;

  @AroundInvoke
  public Object logMethod(InvocationContext context) throws Exception {
    var start = Instant.now();
    var result = context.proceed();
    var et = Duration.between(start, Instant.now()).toMillis();
    var annotation = context.getMethod().getAnnotation(Logged.class);
    if (annotation == null) {
      annotation = context.getMethod().getDeclaringClass().getAnnotation(Logged.class);
    }
    var action = annotation != null ? annotation.value() : "Executed:";
    Object subject = null;;
    if (result instanceof Optional<?> opt) {
      subject = opt.isPresent() ? opt.get() : "Nothing";
    } else if (result instanceof Collection<?> col) {
      subject = col.size() + SPACE;
      if (!annotation.subject().isBlank()) {
        subject += annotation.subject();
      } else {
        subject += "entities";
      }
    } else if (result instanceof Page<?> page) {
      subject = page.content().size() + SPACE;
      if (!annotation.subject().isBlank()) {
        subject += annotation.subject();
      } else {
        subject += "entities";
      }
    } else if (result == null) {
      if (!annotation.subject().isBlank()) {
        subject = annotation.subject();
      } else {
        subject = "";
      }
    } else {
      subject = result;
    }
    LOGGER.infof("%s %s in %d ms", action, subject, et);    
    return result;
  }
}
