package com.erdouglass.emdb.media.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class CommandValidator {
  
  @Inject 
  Validator validator;
  
  public <T> void validate(T command) {
    var violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }  
}
