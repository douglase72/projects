package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.domain.MoviePublicId;

/// Request to freeze or release a title's details.
///
/// Locking is an editorial decision — it marks a title as hand-curated so that
/// automated ingestion cannot overwrite it. It is not concurrency control; the
/// version handles that, and is required here for exactly that reason.
///
/// Unlike the other commands, this one already carries domain value objects: it
/// is built directly at the boundary rather than mapped, because there is
/// nothing to convert beyond the two identifiers.
///
/// @param publicId the catalogue id of the title
/// @param version the version the caller last read
/// @param lock `true` to freeze the details, `false` to release them
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
