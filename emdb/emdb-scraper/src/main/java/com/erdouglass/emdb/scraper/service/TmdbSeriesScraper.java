package com.erdouglass.emdb.scraper.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.Image;
import com.erdouglass.emdb.common.comand.SavePerson;
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
  
  public SaveSeries extract(@NotNull SaveSeries command) {
    var start = Instant.now();
    rateLimiter.acquire();
    var tmdbSeries = client.findById(command.tmdbId(), CREDITS);
    var credits = findCredits(tmdbSeries);
    var ids = Stream.concat(
        credits.cast().stream().map(CastCredit::tmdbId),
        credits.crew().stream().map(CrewCredit::tmdbId))
        .distinct()
        .toList();
    var existingPeople = command.people().stream()
        .collect(Collectors.toMap(SavePerson::tmdbId, Function.identity()));
    var people = findPeople(ids, existingPeople);
    
    // Update the series backdrop and poster if they changed.
    var backdrop = command.backdrop();
    if (backdrop == null || !Objects.equals(tmdbSeries.backdrop_path(), backdrop.tmdbName())) {
      backdrop = Image.of(imageService.save(tmdbSeries.backdrop_path()), tmdbSeries.backdrop_path());
    }
    var poster = command.poster();
    if (poster == null || !Objects.equals(tmdbSeries.poster_path(), poster.tmdbName())) {
      poster = Image.of(imageService.save(tmdbSeries.poster_path()), tmdbSeries.poster_path());
    }
    var cmd = mapper.toSaveSeries(tmdbSeries, backdrop, poster, credits, people);
    var et = Duration.between(start, Instant.now()).toMillis();
    var msg = String.format("Ingest Job for TMDB series %d extracted in %d ms", command.tmdbId(), et);
    LOGGER.info(msg);    
    return cmd;
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
