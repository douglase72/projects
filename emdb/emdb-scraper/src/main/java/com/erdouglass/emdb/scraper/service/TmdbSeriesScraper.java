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
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.SaveSeries.CastCredit;
import com.erdouglass.emdb.common.comand.SaveSeries.Credits;
import com.erdouglass.emdb.common.comand.SaveSeries.CrewCredit;
import com.erdouglass.emdb.scraper.client.TmdbSeriesClient;
import com.erdouglass.emdb.scraper.mapper.TmdbSeriesCreditMapper;
import com.erdouglass.emdb.scraper.mapper.TmdbSeriesMapper;
import com.erdouglass.emdb.scraper.query.TmdbSeries;

@ApplicationScoped
public class TmdbSeriesScraper extends TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbSeriesScraper.class);
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  @RestClient
  TmdbSeriesClient client;
  
  @Inject
  TmdbSeriesCreditMapper creditMapper;
  
  @Inject
  TmdbSeriesMapper mapper;
  
  public SaveSeries extract(@NotNull @Positive Integer tmdbId) {
    return performExtraction(tmdbId);
  }
  
  public SaveSeries extract(@NotNull @Valid SaveMovie command) {
    return performExtraction(command.tmdbId());
  }
  
  private SaveSeries performExtraction(int tmdbId) {
    var start = Instant.now();
    rateLimiter.acquire();
    var tmdbSeries = client.findById(tmdbId, CREDITS);
    var credits = findCredits(tmdbSeries);
    var ids = Stream.concat(
        credits.cast().stream().map(CastCredit::tmdbId),
        credits.crew().stream().map(CrewCredit::tmdbId))
        .distinct()
        .toList();
    var people = findPeople(ids);
    var et = Duration.between(start, Instant.now()).toMillis();
    var msg = String.format("Ingest Job for TMDB series %d extracted in %d ms", tmdbId, et);
    LOGGER.info(msg);    
    var command = mapper.toSaveSeries(
        tmdbSeries, 
        UUID.randomUUID(), 
        UUID.randomUUID(),
        credits,
        people);
    return command;
  }
  
  private Credits findCredits(TmdbSeries series) {
    var cast = series.aggregate_credits().cast().stream()
        .limit(castLimit)
        .map(creditMapper::toCastCredit)
        .toList();
    var crew = series.aggregate_credits().crew().stream()
        .limit(crewLimit)
        .map(creditMapper::toCrewCredit)
        .toList();
    return new Credits(cast, crew);
  }
  
}
