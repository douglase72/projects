package com.erdouglass.emdb.media.service;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.scraper.service.TmdbImageService;

public abstract class MediaService {
  
  @Inject
  TmdbImageService imageService;
  
  @Inject
  Validator validator;
  
  public abstract void ingest(@NotNull @Positive Integer tmdbId, String jobId);
  
  protected <T> void validate(T command) {
    var violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    } 
  }
  
}
