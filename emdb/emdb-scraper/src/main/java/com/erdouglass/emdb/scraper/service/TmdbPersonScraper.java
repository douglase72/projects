package com.erdouglass.emdb.scraper.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.Image;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.scraper.mapper.TmdbPersonMapper;

@ApplicationScoped
public class TmdbPersonScraper extends TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbPersonScraper.class);
  
  @Inject
  TmdbPersonMapper mapper;
  
  public SavePerson extract(@NotNull SavePerson command) {
    var start = Instant.now();
    rateLimiter.acquire();
    var person = personClient.findById(command.tmdbId());
    var profile = command.profile();
    if (profile == null || !Objects.equals(person.profile_path(), profile.tmdbName())) {
      profile = Image.of(imageService.save(person.profile_path()), person.profile_path());
    }
    var cmd = mapper.toSavePerson(person, profile);
    var et = Duration.between(start, Instant.now()).toMillis();
    var msg = String.format("Ingest Job for TMDB person %d extracted in %d ms", command.tmdbId(), et);
    LOGGER.info(msg);    
    return cmd;
  }
  
}
