package com.erdouglass.emdb.media.messaging;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public abstract class Consumer {

  @Inject
  Validator validator;
  
  public abstract void ingest(@NotNull @Positive Integer tmdbId);
  
  protected <T> void validate(T command) {
    var violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    } 
  }
  
}
