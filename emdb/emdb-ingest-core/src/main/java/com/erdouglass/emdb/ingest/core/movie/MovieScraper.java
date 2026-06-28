package com.erdouglass.emdb.ingest.core.movie;

import java.util.Comparator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.core.Scraper;
import com.erdouglass.emdb.ingest.core.image.ImageScraper;
import com.erdouglass.emdb.ingest.logging.Log;
import com.erdouglass.emdb.media.movie.SaveMovie;
import com.erdouglass.emdb.media.movie.SaveMovie.CastCredit;
import com.erdouglass.emdb.media.movie.SaveMovie.Credits;

@ApplicationScoped
class MovieScraper extends Scraper<Movie> {
  private static final String CREDITS = "credits";
  
  @Inject
  @ConfigProperty(name = "tmdb.cast.limit")
  Integer castLimit;
  
  @Inject
  @ConfigProperty(name = "tmdb.crew.limit")
  Integer crewLimit;
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
  @Inject
  ImageScraper imageScraper;
  
  @Inject
  TmdbMovieMapper mapper;
  
  @Inject
  MovieRepository repository;

  @Log
  @Transactional
  public SaveMovie scrape(@NotNull @Positive Integer tmdbId) {
    var tmdbMovie = client.findById(tmdbId, CREDITS);   
    var existing = repository.findById(tmdbId).orElse(null);
    var backdrop = resolveImage(existing, tmdbMovie.backdrop_path(),
        Movie::getTmdbBackdrop, Movie::getEmdbBackdrop);
    var poster = resolveImage(existing, tmdbMovie.poster_path(),
        Movie::getTmdbPoster, Movie::getEmdbPoster);
    var movie = existing != null ? existing : new Movie(tmdbId);
    movie.setEmdbBackdrop(nameOf(backdrop));
    movie.setTmdbBackdrop(tmdbMovie.backdrop_path());
    movie.setEmdbPoster(nameOf(poster));
    movie.setTmdbPoster(tmdbMovie.poster_path());
    repository.save(movie); 
    return limitCredits(mapper.toSaveMovie(tmdbMovie, backdrop, poster));
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
