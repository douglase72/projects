package com.erdouglass.emdb.media.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.scraper.service.TmdbImageService;

public abstract class MediaService {
  private static final Logger LOGGER = Logger.getLogger(MediaService.class);
  
  @Inject
  TmdbImageService imageService;
  
  @Inject
  PersonMapper personMapper;
  
  @Inject
  PersonService personService;
  
  @Inject
  Validator validator;
  
  public abstract void ingest(@NotNull @Positive Integer tmdbId, String jobId);

  protected void savePeople(List<Person> people) {
    var start = System.nanoTime();
    var savedPeople = personService.saveAll(people);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Loaded %d people in %d ms", savedPeople.size(), et);
  }
  
  protected <T> void validate(T command) {
    var violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    } 
  }
  
}
