package com.erdouglass.emdb.media.movie.domain;

import java.util.Objects;
import java.util.UUID;

import com.erdouglass.emdb.media.kernel.ValueObject;

/// The surrogate identity of a movie aggregate.
///
/// Assigned by the application at creation time rather than by the database, so
/// a movie has identity before it is ever written — which is what lets
/// [Movie#equals(Object)] work on unpersisted instances and lets audit rows be
/// attributed correctly within the inserting transaction.
///
/// Expected to be a time-ordered UUIDv7, giving locality on disk without the
/// guessability of a sequence. Never exposed outside the application; clients
/// address titles by [MoviePublicId].
///
/// @param value the UUID, never `null`
public record MovieId(UUID value) implements ValueObject<UUID> {

  public MovieId {
    Objects.requireNonNull(value, "movie id must not be null");
  }
  
  public static MovieId of(UUID id) {
    return new MovieId(id);
  }
}
