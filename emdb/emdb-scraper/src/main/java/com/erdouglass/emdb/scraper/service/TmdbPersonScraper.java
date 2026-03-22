package com.erdouglass.emdb.scraper.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.scraper.mapper.TmdbPersonMapper;

@ApplicationScoped
public class TmdbPersonScraper extends TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbPersonScraper.class);
  
  @Inject
  TmdbPersonMapper mapper;
  
  public SavePerson extract(@NotNull @Positive Integer tmdbId) {
    return performExtraction(tmdbId);
  }
  
  public SavePerson extract(@NotNull @Valid SavePerson command) {
    return performExtraction(command.tmdbId());
  }
  
  private SavePerson performExtraction(int tmdbId) {
    var start = Instant.now();
    rateLimiter.acquire();
    var tmdbPerson = personClient.findById(tmdbId);
    var et = Duration.between(start, Instant.now()).toMillis();
    var msg = String.format("Ingest Job for TMDB person %d extracted in %d ms", tmdbId, et);
    LOGGER.info(msg);    
    var command = mapper.toSavePerson(tmdbPerson, UUID.randomUUID());
    return command;
  }
  
}
