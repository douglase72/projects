package com.erdouglass.emdb.ingest.scraper.movie;

import java.util.Comparator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.scraper.Scraper;
import com.erdouglass.emdb.ingest.scraper.image.ImageScraper;
import com.erdouglass.emdb.media.command.SaveMovie;
import com.erdouglass.emdb.media.command.SaveMovie.CastCredit;
import com.erdouglass.emdb.media.command.SaveMovie.Credits;

/// [Scraper] implementation that fetches movies from TMDB and emits the
/// resulting [SaveMovie] commands on the `save-movie-out` channel.
@ApplicationScoped
class MovieScraper extends Scraper<SaveMovie> {
  private static final String CREDITS = "credits";
  
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;
  
  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;

  @Inject
  @RestClient
  MovieClient client;
  
  @Inject
  @Channel("save-movie-out") 
  Emitter<SaveMovie> emitter;
  
  @Inject
  ImageScraper imageScraper;
  
  @Inject
  MovieMapper mapper;
  
  @Override
  protected Emitter<SaveMovie> emitter() {
    return emitter;
  }
  
  @Override
  protected SaveMovie extract(final int tmdbId) {
    var movie = client.findById(tmdbId, CREDITS);
    var backdrop = imageScraper.extract(movie.backdrop_path());
    var poster = imageScraper.extract(movie.poster_path());
    var command = limitCredits(mapper.toSaveMovie(movie, backdrop, poster));
    return command;
  }
  
  private SaveMovie limitCredits(SaveMovie command) {
    var cast = command.credits().cast().stream()
        .sorted(Comparator.comparingInt(CastCredit::order))
        .limit(castLimit)
        .toList();
    var crew = command.credits().crew().stream()
        .limit(crewLimit)
        .toList();
    return SaveMovie.builder(command)
        .credits(new Credits(cast, crew))
        .build();
  }
}
