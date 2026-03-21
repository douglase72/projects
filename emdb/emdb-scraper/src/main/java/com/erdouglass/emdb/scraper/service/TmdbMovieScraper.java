package com.erdouglass.emdb.scraper.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.SaveMovie.CastCredit;
import com.erdouglass.emdb.common.comand.SaveMovie.Credits;
import com.erdouglass.emdb.common.comand.SaveMovie.CrewCredit;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.mapper.TmdbCreditMapper;
import com.erdouglass.emdb.scraper.mapper.TmdbMovieMapper;
import com.erdouglass.emdb.scraper.query.TmdbMovie;

@ApplicationScoped
public class TmdbMovieScraper extends TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbMovieScraper.class);
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
  @Inject
  TmdbCreditMapper creditMapper;
  
  @Inject
  TmdbMovieMapper mapper;
  
  @Inject
  TmdbRateLimiter rateLimiter;
  
  public SaveMovie extract(@NotNull @Positive Integer tmdbId) {
    return performExtraction(tmdbId);
  }
  
  public SaveMovie extract(@NotNull @Valid SaveMovie command) {
    return performExtraction(command.tmdbId());
  }
  
  private SaveMovie performExtraction(int tmdbId) {
    var start = Instant.now();
    rateLimiter.acquire();
    var tmdbMovie = client.findById(tmdbId, CREDITS);
    var credits = findCredits(tmdbMovie);
    var ids = Stream.concat(
        credits.cast().stream().map(CastCredit::tmdbId),
        credits.crew().stream().map(CrewCredit::tmdbId))
        .distinct()
        .toList();
    var people = findPeople(ids);
    var et = Duration.between(start, Instant.now()).toMillis();
    var msg = String.format("Ingest Job for TMDB movie %d extracted in %d ms", tmdbId, et);
    LOGGER.info(msg);    
    var command = mapper.toSaveMovie(
        tmdbMovie, 
        UUID.randomUUID(), 
        UUID.randomUUID(),
        credits,
        people,
        -1f);
    return command;
  }
  
  private Credits findCredits(TmdbMovie movie) {
    var cast = movie.credits().cast().stream()
        .limit(castLimit)
        .map(creditMapper::toCastCredit)
        .toList();
    var crew = movie.credits().crew().stream()
        .limit(crewLimit)
        .map(creditMapper::toCrewCredit)
        .toList();
    return new Credits(cast, crew);
  }

}
