package com.erdouglass.emdb.scraper.service;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.common.Image;
import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.SaveMovie;
import com.erdouglass.emdb.common.event.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatus;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusContext;
import com.erdouglass.emdb.scraper.annotation.UpdateScraperStatus;
import com.erdouglass.emdb.scraper.client.TmdbMovieClient;
import com.erdouglass.emdb.scraper.mapper.TmdbMovieMapper;

@ApplicationScoped
public class TmdbMovieScraper {
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
  @Inject
  TmdbMovieMapper mapper;
  
  @Inject 
  IngestStatusContext statusContext;

  @UpdateScraperStatus
  public SaveMovie extract(@NotNull SaveMovie command) {
    var tmdbMovie = client.findById(command.tmdbId(), CREDITS);
    
    // Update the movie backdrop and poster if they changed.
    var backdrop = Image.of(UUID.randomUUID(), tmdbMovie.backdrop_path());
    var poster = Image.of(UUID.randomUUID(), tmdbMovie.poster_path());
    
    statusContext.set(IngestStatusChanged.builder()
        .tmdbId(tmdbMovie.id())
        .status(IngestStatus.EXTRACTED)
        .source(IngestSource.SCRAPER)
        .type(MediaType.MOVIE)
        .name(tmdbMovie.title())
        .message(String.format("Ingest Job for TMDB movie %d fetched from TMDB", tmdbMovie.id()))
        .build());
    var cmd = mapper.toSaveMovie(tmdbMovie, backdrop, poster);
    return cmd;
  }
}
