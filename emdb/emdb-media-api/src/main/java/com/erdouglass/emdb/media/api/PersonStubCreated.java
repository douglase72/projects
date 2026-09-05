package com.erdouglass.emdb.media.api;

import java.util.Objects;

import com.erdouglass.common.util.DateTime;

public record PersonStubCreated(
    EventId id, 
    DateTime occurredAt,
    TmdbId tmdbId) {

  public PersonStubCreated {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(occurredAt, "occurredAt is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
  }
  
  public static PersonStubCreated of(EventId id, DateTime occurredAt, TmdbId tmdbId) {
    return new PersonStubCreated(id, occurredAt, tmdbId);
  }
}
