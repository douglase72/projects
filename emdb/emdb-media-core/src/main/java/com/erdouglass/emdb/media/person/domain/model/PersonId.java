package com.erdouglass.emdb.media.person.domain.model;

import java.util.Objects;
import java.util.UUID;

import com.erdouglass.emdb.media.kernel.AggregateId;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

public record PersonId(UUID value) implements AggregateId {
  private static final TimeBasedEpochGenerator ID_GENERATOR = Generators.timeBasedEpochGenerator();

  public PersonId {
    Objects.requireNonNull(value, "person id must not be null");
  }
  
  public static PersonId newId() { return new PersonId(ID_GENERATOR.generate()); }
  public static PersonId of(UUID id) { return new PersonId(id); }
}
