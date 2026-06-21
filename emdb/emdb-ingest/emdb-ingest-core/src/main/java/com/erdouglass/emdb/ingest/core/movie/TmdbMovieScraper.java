package com.erdouglass.emdb.ingest.core.movie;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.core.Log;
import com.erdouglass.emdb.ingest.core.TmdbImageScraper;
import com.erdouglass.emdb.ingest.ws.rest.TmdbMovieClient;
import com.erdouglass.emdb.media.Image;
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
    var command = mapper.toSaveMovie(tmdbMovie, backdrop, poster);
    return command;
  }
  
  private Image resolveImage(
      Movie existing, 
      String newPath,
      Function<Movie, String> tmdbPathOf, 
      Function<Movie, UUID> emdbNameOf) {
    if (existing == null || !Objects.equals(tmdbPathOf.apply(existing), newPath)) {
      return imageScraper.scrape(newPath);
    }
    return new Image(emdbNameOf.apply(existing), null);
  } 
  
  private static UUID nameOf(Image image) {
    return image == null ? null : image.name();
  }
}
