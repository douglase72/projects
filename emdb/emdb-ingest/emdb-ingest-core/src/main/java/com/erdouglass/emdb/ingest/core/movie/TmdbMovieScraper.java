package com.erdouglass.emdb.ingest.core.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.core.Log;
import com.erdouglass.emdb.ingest.core.TmdbImageScraper;
import com.erdouglass.emdb.ingest.ws.rest.TmdbMovieClient;
import com.erdouglass.emdb.media.command.SaveMovie;

@ApplicationScoped
class TmdbMovieScraper {
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
  @Inject
  TmdbImageScraper imageScraper;
  
  @Inject
  TmdbMovieMapper mapper;

  @Log
  public SaveMovie scrape(@NotNull @Positive Integer tmdbId) {
    var movie = client.findById(tmdbId, CREDITS);
    var backdrop = imageScraper.scrape(movie.backdrop_path());
    var poster = imageScraper.scrape(movie.poster_path());
    var command = mapper.toSaveMovie(movie, backdrop, poster);
    return command;
  }
}
