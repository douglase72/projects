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

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.scraper.client.TmdbSeriesClient;

@ApplicationScoped
public class TmdbSeriesScraper {
  private static final Logger LOGGER = Logger.getLogger(TmdbSeriesScraper.class);
  private static final String CREDITS = "aggregate_credits";
  
  @Inject
  @RestClient
  TmdbSeriesClient client;
  
  @Inject
  TmdbImageService imageService;  
  
  public SaveSeries scrape(@NotNull @Valid SaveSeries command, @NotBlank String jobId) {
    var start = System.nanoTime();
    var tmdbSeries = client.findById(command.tmdbId(), CREDITS);
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
    return cmd.build();    
  }
  
}
