package com.erdouglass.emdb.media.application.port.inbound.movie;

import java.util.Objects;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.domain.movie.MovieDetails;

public record SaveMovieCommand(TmdbId tmdbId, MovieDetails details) {
  
  public SaveMovieCommand {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(details, "movie details are reqired");
  }
  
  public static SaveMovieCommand of(TmdbId tmdbId, MovieDetails details) {
    return new SaveMovieCommand(tmdbId, details);
  }
}
