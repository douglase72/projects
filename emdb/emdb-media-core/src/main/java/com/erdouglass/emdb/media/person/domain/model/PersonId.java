package com.erdouglass.emdb.media.person.domain.model;

import java.util.Objects;
import java.util.UUID;

import com.erdouglass.emdb.media.kernel.AggregateId;

public record PersonId(UUID value) implements AggregateId {

  public PersonId {
    Objects.requireNonNull(value, "person id must not be null");
  }
  
  public static PersonId newId() { return new PersonId(UUID.randomUUID()); }
  public static PersonId of(UUID id) { return new PersonId(id); }
}
