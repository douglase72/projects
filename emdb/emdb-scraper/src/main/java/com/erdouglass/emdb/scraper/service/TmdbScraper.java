package com.erdouglass.emdb.scraper.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.scraper.client.TmdbPersonClient;
import com.erdouglass.emdb.scraper.query.TmdbPersonDto;

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
  @RestClient
  TmdbPersonClient personClient; 
  
  @Inject
  TmdbRateLimiter rateLimiter;
  
  protected Map<Integer, SavePerson> findPeople(List<Integer> ids, Map<Integer, SavePerson> existingPeople) {
    var start = System.nanoTime();  
    var tasks = ids.stream()
        .map(id -> CompletableFuture.supplyAsync(() -> {
          rateLimiter.acquire();
          return personClient.findById(id);
        }, executor)
        .thenApplyAsync(dto -> {
          var existing = existingPeople.get(dto.id());
          return toSavePerson(dto, existing);
        }, executor))
        .toList();
    var people = tasks.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toMap(SavePerson::tmdbId, Function.identity()));
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Extracted %d people in %d ms", people.size(), et);
    return people;
  }
  
  protected SavePerson toSavePerson(TmdbPersonDto person, SavePerson command) {
    var cmd = SavePerson.builder()
        .tmdbId(person.id())
        .name(person.name())
        .birthDate(person.birthday())
        .deathDate(person.deathday())
        .gender(Gender.from(person.gender()))
        .birthPlace(person.place_of_birth())
        .biography(person.biography());
    if (command == null || !Objects.equals(person.profile_path(), command.tmdbProfile())) {
      cmd.profile(imageService.save(person.profile_path()))
        .tmdbProfile(person.profile_path());
    } else {
      cmd.profile(command.profile())
        .tmdbProfile(command.tmdbProfile());
    }
    return cmd.build();
  } 
  
}
