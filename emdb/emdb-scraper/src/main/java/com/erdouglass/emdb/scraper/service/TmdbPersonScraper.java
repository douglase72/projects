package com.erdouglass.emdb.scraper.service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.Gender;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.scraper.client.TmdbPersonClient;

import io.micrometer.core.annotation.Timed;

@ApplicationScoped
public class TmdbPersonScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbPersonScraper.class);
  
  @Inject
  @RestClient
  TmdbPersonClient client;
  
  @Inject
  TmdbImageService imageService;
  
  @Timed(
      value = "emdb.scrape.duration", 
      extraTags = {"media", "person"}
  )
  public SavePerson scrape(@NotNull @Valid SavePerson command, @NotBlank String jobId) {
    var start = System.nanoTime();
    var tmdbPerson = client.findById(command.tmdbId());
    var cmd = SavePerson.builder()
        .tmdbId(tmdbPerson.id())
        .name(tmdbPerson.name())
        .birthDate(tmdbPerson.birthday())
        .deathDate(tmdbPerson.deathday())
        .gender(Gender.from(tmdbPerson.gender()))
        .birthPlace(tmdbPerson.place_of_birth())
        .profile(command.profile())
        .tmdbProfile(command.tmdbProfile())
        .biography(tmdbPerson.biography());
    if (!Objects.equals(tmdbPerson.profile_path(), command.tmdbProfile())) {
      cmd.profile(imageService.save(tmdbPerson.profile_path()))
        .tmdbProfile(tmdbPerson.profile_path());
    }    
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Ingest Job %s for TMDB person %d extracted in %d ms", jobId, command.tmdbId(), et);      
    return cmd.build();
  }
  
}
