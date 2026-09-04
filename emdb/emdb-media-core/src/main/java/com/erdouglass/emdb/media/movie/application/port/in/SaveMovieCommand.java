package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.List;
import java.util.Objects;

import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;

public record SaveMovieCommand(
    TmdbId tmdbId,
    MovieDetails details,
    List<CreditSpec> credits) {
  
  public SaveMovieCommand {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(details, "movie details are required");
    Objects.requireNonNull(credits, "movie credits are required");
  }
  
  public static SaveMovieCommand of(TmdbId tmdbId, MovieDetails details, List<CreditSpec> credits) {
    return new SaveMovieCommand(tmdbId, details, credits);
  }
}
