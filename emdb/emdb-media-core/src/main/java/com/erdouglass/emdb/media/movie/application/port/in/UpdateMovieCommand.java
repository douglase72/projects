package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;

public record UpdateMovieCommand(PublicId id, Version version, MovieDetails details) { 
  
  public UpdateMovieCommand {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(details, "details are required");
  }
  
  public static UpdateMovieCommand of(PublicId id, Version version, MovieDetails details) {
    return new UpdateMovieCommand(id, version, details);
  }
}
