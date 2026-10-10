package com.erdouglass.emdb.media.shared.domain.model;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

public record MediaId(UUID value) {
  private static final TimeBasedEpochGenerator ID_GENERATOR = Generators.timeBasedEpochGenerator();
  
  public MediaId {
    Objects.requireNonNull(value, "id is required");
  }
  
  public static MediaId newId() { return new MediaId(ID_GENERATOR.generate()); }
  public static MediaId of(UUID id) { return new MediaId(id); }
}
