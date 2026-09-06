package com.erdouglass.emdb.media.kernel;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

public record PublicId(UUID value) {
  private static final TimeBasedEpochGenerator ID_GENERATOR = Generators.timeBasedEpochGenerator();
  
  public PublicId {
    Objects.requireNonNull(value, "id is required");
  }
  
  public static PublicId newId() { return new PublicId(ID_GENERATOR.generate()); }
  public static PublicId of(UUID id) { return new PublicId(id); }
}
