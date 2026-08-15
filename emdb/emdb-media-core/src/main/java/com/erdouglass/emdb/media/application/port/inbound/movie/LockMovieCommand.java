package com.erdouglass.emdb.media.application.port.inbound.movie;

import java.util.Objects;

import com.erdouglass.emdb.media.domain.movie.MoviePublicId;
import com.erdouglass.emdb.media.domain.shared.Version;

public record LockMovieCommand(
    MoviePublicId publicId,
    Version version,
    Boolean lock) {

  public LockMovieCommand {
    Objects.requireNonNull(publicId, "publicId is required");
    Objects.requireNonNull(version, "version is required");    
    Objects.requireNonNull(lock, "lock details are reqired");
  }
}
