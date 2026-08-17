package com.erdouglass.emdb.media.person.domain;

import java.util.Objects;
import java.util.UUID;

import com.erdouglass.emdb.media.kernel.ValueObject;

/// The surrogate identity of a person aggregate.
///
/// Assigned by the application at creation time rather than by the database, so
/// a person has identity before it is ever written — which is what lets
/// [Person#equals(Object)] work on unpersisted instances and lets audit rows be
/// attributed correctly within the inserting transaction.
///
/// Expected to be a time-ordered UUIDv7, giving locality on disk without the
/// guessability of a sequence. Never exposed outside the application; clients
/// address titles by [PersonPublicId].
///
/// @param value the UUID, never `null`
public record PersonId(UUID value) implements ValueObject<UUID> {

  public PersonId {
    Objects.requireNonNull(value, "person id must not be null");
  }
  
  public static PersonId of(UUID id) {
    return new PersonId(id);
  }
}
