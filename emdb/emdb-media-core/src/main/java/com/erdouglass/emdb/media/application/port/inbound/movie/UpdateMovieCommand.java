package com.erdouglass.emdb.media.application.port.inbound.movie;

import java.util.Objects;

import com.erdouglass.emdb.media.domain.movie.MovieDetails;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;
import com.erdouglass.emdb.media.domain.shared.Version;

public record UpdateMovieCommand(
    MoviePublicId publicId,
    Version version, 
    MovieDetails details) {

  public UpdateMovieCommand {
    Objects.requireNonNull(publicId, "publicId is required");
    Objects.requireNonNull(version, "version is required");    
    Objects.requireNonNull(details, "movie details are reqired");
  }
  
  public static UpdateMovieCommand of(MoviePublicId publicId, Version version, MovieDetails details) {
    return new UpdateMovieCommand(publicId, version, details);
  }
}
