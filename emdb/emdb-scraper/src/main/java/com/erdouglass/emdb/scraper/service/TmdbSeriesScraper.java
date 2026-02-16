package com.erdouglass.emdb.scraper.service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.scraper.client.TmdbSeriesClient;

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
        .overview(tmdbSeries.overview());
    if (!Objects.equals(tmdbSeries.backdrop_path(), command.tmdbBackdrop())) {
      cmd.backdrop(imageService.save(tmdbSeries.backdrop_path()))
        .tmdbBackdrop(tmdbSeries.backdrop_path());
    }
    if (!Objects.equals(tmdbSeries.poster_path(), command.tmdbPoster())) {
      cmd.poster(imageService.save(tmdbSeries.poster_path()))
        .tmdbPoster(tmdbSeries.poster_path());
    }    
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Ingest Job %s for TMDB series %d extracted in %d ms", jobId, command.tmdbId(), et);
    statusService.send(IngestStatusChanged.builder()
        .id(jobId)
        .status(IngestStatus.EXTRACTED)
        .tmdbId(command.tmdbId())
        .source(IngestSource.SCRAPER)
        .type(MediaType.SERIES)
        .name(tmdbSeries.name())
        .build());    
    return cmd.build();    
  }
  
}
