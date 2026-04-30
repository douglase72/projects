package com.erdouglass.emdb.scraper.service;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.Image;
import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.event.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatus;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusContext;
import com.erdouglass.emdb.scraper.annotation.UpdateScraperStatus;
import com.erdouglass.emdb.scraper.client.TmdbPersonClient;
import com.erdouglass.emdb.scraper.mapper.TmdbPersonMapper;

@ApplicationScoped
public class TmdbPersonScraper {

  @Inject
  @RestClient
  TmdbPersonClient client;
  
  @Inject
  TmdbPersonMapper mapper;
  
  @Inject 
  IngestStatusContext statusContext;
  
  @UpdateScraperStatus
  public SavePerson extract(@NotNull SavePerson command) {
    var tmdbPerson = client.findById(command.tmdbId());
    
    // Update the persons profile if it changed.
    var profile = Image.of(UUID.randomUUID(), tmdbPerson.profile_path());
    
    statusContext.set(IngestStatusChanged.builder()
        .tmdbId(tmdbPerson.id())
        .status(IngestStatus.EXTRACTED)
        .source(IngestSource.SCRAPER)
        .type(MediaType.PERSON)
        .name(tmdbPerson.name())
        .message(String.format("Ingest Job for TMDB person %d fetched from TMDB", tmdbPerson.id()))
        .build());
    var cmd = mapper.toSavePerson(tmdbPerson, profile);
    return cmd;    
  }
}
