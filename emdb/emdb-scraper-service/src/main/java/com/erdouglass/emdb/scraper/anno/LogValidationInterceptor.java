package com.erdouglass.emdb.scraper.anno;

import java.util.stream.Collectors;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

@LogValidation
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LogValidationInterceptor {

  @AroundInvoke
  public Object handle(InvocationContext ctx) throws Exception {
    try {
      return ctx.proceed();
    } catch (ConstraintViolationException e) {
      var violations = e.getConstraintViolations();
      var details = violations.stream()
          .map(v -> String.format("%s %s (value: '%s')", 
              v.getPropertyPath(), 
              v.getMessage(), 
              v.getInvalidValue()))
          .collect(Collectors.joining(", "));
      var msg = String.format("%d constraint violation(s) occurred during method validation.\n%s", 
          violations.size(), details);
      throw new ValidationException(msg, e);
    }
  }

}
