package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public record SaveMovieCommand(TmdbId tmdbId, MovieDetails details) {

  public SaveMovieCommand {
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(details, "details are required");
  }
  
  public static SaveMovieCommand of(TmdbId tmdbId, MovieDetails details) {
    return new SaveMovieCommand(tmdbId, details);
  }
}
