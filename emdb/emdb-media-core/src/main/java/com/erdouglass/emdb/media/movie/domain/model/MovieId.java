package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;
import java.util.UUID;

import com.erdouglass.emdb.media.kernel.ValueObject;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

public record MovieId(UUID value) implements ValueObject<UUID> {
  private static final TimeBasedEpochGenerator ID_GENERATOR = Generators.timeBasedEpochGenerator();
  
  public MovieId {
    Objects.requireNonNull(value, "movie id must not be null");
  }
  
  public static MovieId newId() { return new MovieId(ID_GENERATOR.generate()); }
  public static MovieId of(UUID id) { return new MovieId(id); }
}
