package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.shared.kernel.PublicId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public record SaveMovieCommand(
    PublicId correlationId,
    TmdbId tmdbId) {

  public SaveMovieCommand {
    Objects.requireNonNull(correlationId, "correlationId is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
  }
  
  public static SaveMovieCommand of(TmdbId tmdbId) {
    return new SaveMovieCommand(PublicId.newId(), tmdbId);
  }
  
  public static SaveMovieCommand of(PublicId correlationId, TmdbId tmdbId) {
    return new SaveMovieCommand(correlationId, tmdbId);
  }
}
