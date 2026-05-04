package com.erdouglass.emdb.media.service;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.media.annotation.ExtractionStatus;
import com.erdouglass.emdb.media.api.Image;
import com.erdouglass.emdb.media.api.command.SaveMovie;
import com.erdouglass.emdb.media.client.TmdbMovieClient;
import com.erdouglass.emdb.media.entity.Movie;
import com.erdouglass.emdb.media.mapper.MovieMapper;

@ApplicationScoped
public class TmdbMovieScraper {
  private static final String CREDITS = "credits";
  
  @Inject
  @RestClient
  TmdbMovieClient client;
  
  @Inject
  MovieMapper mapper;
  
  @ExtractionStatus
  public SaveMovie extract(@NotNull Movie movie) {
    var tmdbMovie = client.findById(movie.getTmdbId(), CREDITS);    
    var backdrop = Image.of(UUID.randomUUID(), tmdbMovie.backdrop_path());
    var poster = Image.of(UUID.randomUUID(), tmdbMovie.poster_path());    
    return mapper.toSaveMovie(tmdbMovie, backdrop, poster);
  }
}
