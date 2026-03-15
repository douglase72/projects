package com.erdouglass.emdb.scraper.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.mapper.TmdbMapper;

@ApplicationScoped
public class TmdbMovieScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
  @Inject
  TmdbMapper mapper;
  
  public SaveMovie extract(@NotNull @Positive Integer tmdbId) {
    return performExtraction(tmdbId);
  }
  
  public SaveMovie extract(@NotNull @Valid SaveMovie command) {
    return performExtraction(command.tmdbId());
  }
  
  private SaveMovie performExtraction(int tmdbId) {
    var start = Instant.now();
    var tmdbMovie = client.findById(tmdbId, CREDITS);
    var et = Duration.between(start, Instant.now()).toMillis();
    var msg = String.format("Ingest Job for TMDB movie %d extracted in %d ms", tmdbId, et);
    LOGGER.info(msg);    
    var command = mapper.toSaveMovie(tmdbMovie, UUID.randomUUID(), UUID.randomUUID());
    return command;
  }

}
