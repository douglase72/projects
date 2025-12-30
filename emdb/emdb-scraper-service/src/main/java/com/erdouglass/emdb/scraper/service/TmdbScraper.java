package com.erdouglass.emdb.scraper.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.PersonCreateDto;
import com.erdouglass.emdb.scraper.client.TmdbPersonClient;
import com.erdouglass.emdb.scraper.mapper.TmdbPersonMapper;
import com.erdouglass.emdb.scraper.query.TmdbPersonDto;
import com.google.common.util.concurrent.RateLimiter;

public abstract class TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbScraper.class);
  
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;
  
  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;
  
  @Inject
  @ConfigProperty(name = "tmdb.rate.limit")
  Integer rateLimit;
  
  @Inject
  @RestClient
  TmdbPersonClient personClient;
  
  @Inject
  TmdbPersonMapper personMapper;
  
  @Inject
  Validator validator;
  
  protected Map<Integer, PersonCreateDto> findPeople(Stream<Integer> ids, int tmdbId) {
    var rateLimiter = RateLimiter.create(rateLimit);
    var startTime = System.nanoTime();
    var people = ids.distinct()
        .map(id -> {
          rateLimiter.acquire(); 
          return personMapper.toPersonCreateDto(findPerson(id));
        })
        .collect(Collectors.toMap(PersonCreateDto::tmdbId, Function.identity()));
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    LOGGER.infof("Fetched %d people for TMDB movie %d in %d ms", people.size(), tmdbId, et);
    return people;
  }
  
  private TmdbPersonDto findPerson(int tmdbId) {
    var tmdbPerson = personClient.findById(tmdbId);
    var violations = validator.validate(tmdbPerson);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
    return tmdbPerson;
  }
  
}
