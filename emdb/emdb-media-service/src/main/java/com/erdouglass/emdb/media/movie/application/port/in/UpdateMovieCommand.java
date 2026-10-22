package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.shared.domain.model.MediaId;
import com.erdouglass.emdb.media.shared.domain.model.Version;

public record UpdateMovieCommand(MediaId id, Version version, MovieDetails details) { 
  
  public UpdateMovieCommand {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(details, "details are required");
  }
  
  public static UpdateMovieCommand of(MediaId id, Version version, MovieDetails details) {
    return new UpdateMovieCommand(id, version, details);
  }
}
