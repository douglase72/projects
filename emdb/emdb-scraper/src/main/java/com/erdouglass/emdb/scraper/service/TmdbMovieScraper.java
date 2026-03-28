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
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.comand.SaveMovie.CastCredit;
import com.erdouglass.emdb.common.comand.SaveMovie.Credits;
import com.erdouglass.emdb.common.comand.SaveMovie.CrewCredit;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.mapper.TmdbMovieCreditMapper;
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
  TmdbMovieCreditMapper creditMapper;
  
  @Inject
  TmdbMovieMapper mapper;
  
  public SaveMovie extract(@NotNull SaveMovie command) {
    var start = Instant.now();
    rateLimiter.acquire();
    var tmdbMovie = client.findById(command.tmdbId(), CREDITS);
    var credits = findCredits(tmdbMovie);
    var ids = Stream.concat(
        credits.cast().stream().map(CastCredit::tmdbId),
        credits.crew().stream().map(CrewCredit::tmdbId))
        .distinct()
        .toList();
    var existingPeople = command.people().stream()
        .collect(Collectors.toMap(SavePerson::tmdbId, Function.identity()));
    var people = findPeople(ids, existingPeople);
    
    // Update the movie backdrop and poster if they changed.
    var backdrop = command.backdrop();
    if (backdrop == null || !Objects.equals(tmdbMovie.backdrop_path(), backdrop.tmdbName())) {
      backdrop = Image.of(imageService.save(tmdbMovie.backdrop_path()), tmdbMovie.backdrop_path());
    }
    var poster = command.poster();
    if (poster == null || !Objects.equals(tmdbMovie.poster_path(), poster.tmdbName())) {
      poster = Image.of(imageService.save(tmdbMovie.poster_path()), tmdbMovie.poster_path());
    }    
    var cmd = mapper.toSaveMovie(tmdbMovie, backdrop, poster, credits, people);
    var et = Duration.between(start, Instant.now()).toMillis();
    var msg = String.format("Ingest Job for TMDB movie %d extracted in %d ms", command.tmdbId(), et);
    LOGGER.info(msg);
    return cmd;    
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
