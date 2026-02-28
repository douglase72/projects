package com.erdouglass.emdb.scraper.service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.SaveSeries.Credits;
import com.erdouglass.emdb.common.comand.SaveSeriesCastCredit;
import com.erdouglass.emdb.common.comand.SaveSeriesCastCredit.Role;
import com.erdouglass.emdb.common.comand.SaveSeriesCrewCredit;
import com.erdouglass.emdb.common.comand.SaveSeriesCrewCredit.Job;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.scraper.client.TmdbSeriesClient;
import com.erdouglass.emdb.scraper.query.TmdbSeriesDto;
import com.erdouglass.emdb.scraper.query.TmdbSeriesDto.CastCredit;
import com.erdouglass.emdb.scraper.query.TmdbSeriesDto.CrewCredit;

import io.micrometer.core.annotation.Timed;

@ApplicationScoped
public class TmdbSeriesScraper extends TmdbScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbSeriesScraper.class);
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  @RestClient
  TmdbSeriesClient client;
  
  @Inject
  TmdbImageService imageService;  
  
  @Timed(
      value = "emdb.scrape.duration", 
      extraTags = {"media", "series"}
  )
  public SaveSeries scrape(@NotNull @Valid SaveSeries command, @NotNull UUID jobId) {
    var start = System.nanoTime();    
    var tmdbSeries = client.findById(command.tmdbId(), CREDITS);
    var ids = Stream.concat(
        tmdbSeries.aggregate_credits().cast().stream().limit(castLimit).map(CastCredit::id), 
        tmdbSeries.aggregate_credits().crew().stream().limit(crewLimit).map(CrewCredit::id))
        .distinct()
        .toList(); 
    var existingPeople = command.people().stream()
        .collect(Collectors.toMap(SavePerson::tmdbId, Function.identity(), (existing, _) -> existing));
    var people = findPeople(ids, existingPeople);
    var credits = createCredits(tmdbSeries, people); 
    
    // Create the command to save the series.
    var cmd = SaveSeries.builder()
        .tmdbId(tmdbSeries.id())
        .title(tmdbSeries.name())
        .score(tmdbSeries.vote_average())
        .status(tmdbSeries.status())
        .type(tmdbSeries.type())
        .homepage(tmdbSeries.homepage())
        .originalLanguage(tmdbSeries.original_language())
        .backdrop(command.backdrop())
        .tmdbBackdrop(command.tmdbBackdrop())
        .poster(command.poster())
        .tmdbPoster(command.tmdbPoster())
        .tagline(tmdbSeries.tagline())
        .overview(tmdbSeries.overview())
        .credits(credits);
    if (!Objects.equals(tmdbSeries.backdrop_path(), command.tmdbBackdrop())) {
      cmd.backdrop(imageService.save(tmdbSeries.backdrop_path()))
        .tmdbBackdrop(tmdbSeries.backdrop_path());
    }
    if (!Objects.equals(tmdbSeries.poster_path(), command.tmdbPoster())) {
      cmd.poster(imageService.save(tmdbSeries.poster_path()))
        .tmdbPoster(tmdbSeries.poster_path());
    }    
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    var msg = String.format("Ingest Job for TMDB series %d extracted in %d ms", tmdbSeries.id(), et);
    LOGGER.info(msg); 
    statusService.send(IngestStatusChanged.builder()
        .id(jobId)
        .status(IngestStatus.EXTRACTED)
        .tmdbId(command.tmdbId())
        .source(IngestSource.SCRAPER)
        .type(MediaType.SERIES)
        .name(tmdbSeries.name())
        .message(msg)
        .build());    
    return cmd.build();    
  }
  
  private Credits createCredits(TmdbSeriesDto series, Map<Integer, SavePerson> people) {
    var cast = series.aggregate_credits().cast().stream()
        .limit(castLimit)
        .map(c -> SaveSeriesCastCredit.builder()
            .person(people.get(c.id()))
            .roles(c.roles().stream().map(r -> Role.of(r.character(), r.episode_count())).toList())
            .order(c.order())
            .build())
        .toList();
    var crew = series.aggregate_credits().crew().stream()
        .limit(crewLimit)
        .map(c -> SaveSeriesCrewCredit.builder()
            .person(people.get(c.id()))
            .jobs(c.jobs().stream().map(j -> Job.of(j.job(), j.episode_count())).toList())
            .build())
        .toList();
    var credits = new Credits(cast, crew);
    LOGGER.info(String.format("Found %d credits in TMDB series %d", cast.size() + crew.size(), series.id()));
    return credits;
  }
  
}
