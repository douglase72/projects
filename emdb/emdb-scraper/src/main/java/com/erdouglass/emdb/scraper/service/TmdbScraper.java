package com.erdouglass.emdb.scraper.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.Image;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.scraper.client.TmdbPersonClient;
import com.erdouglass.emdb.scraper.mapper.TmdbPersonMapper;

import io.quarkus.virtual.threads.VirtualThreads;

public abstract class TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbScraper.class);
  
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;
  
  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;
  
  @Inject
  @VirtualThreads 
  ExecutorService executor;  
  
  @Inject
  TmdbImageService imageService;
  
  @Inject
  TmdbPersonMapper mapper;
  
  @Inject
  @RestClient
  TmdbPersonClient personClient;
  
  @Inject
  TmdbRateLimiter rateLimiter;
  
  protected List<SavePerson> findPeople(List<Integer> ids, Map<Integer, SavePerson> existingPeople) {
    var start = Instant.now();
    
    var tasks = ids.stream().map(id -> CompletableFuture.supplyAsync(() -> {
        rateLimiter.acquire();
        var person = personClient.findById(id);
        var command = existingPeople.get(person.id());
        var profile = Optional.ofNullable(command).map(SavePerson::profile).orElse(null);
        if (profile == null || !Objects.equals(person.profile_path(), profile.tmdbName())) {
          if (person.profile_path() != null) {
            profile = Image.of(imageService.save(person.profile_path()), person.profile_path());
          }
        }
        return mapper.toSavePerson(person, profile);
      }, executor))
      .toList();
      
      var results = tasks.stream()
          .map(CompletableFuture::join)
          .toList();
      var et = Duration.between(start, Instant.now()).toMillis();
      double rate = tasks.size() * 1000.0 / et;
      LOGGER.infof("Extracted %d people in %d ms (%.2f requests/second)", tasks.size(), et, rate);
      return results;
  }

}
