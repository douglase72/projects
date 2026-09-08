package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;

public record SaveMovieCommand(TmdbId tmdbId, MovieDetails details) { 
  
  public SaveMovieCommand {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(details, "details are required");
  }
  
  public static SaveMovieCommand of(TmdbId tmdbId, MovieDetails details) {
    return new SaveMovieCommand(tmdbId, details);
  }
}
