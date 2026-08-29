package com.erdouglass.emdb.media.kernel;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

public record CreditId(UUID value) {
  private static final TimeBasedEpochGenerator ID_GENERATOR = Generators.timeBasedEpochGenerator();
  
  public CreditId {
    Objects.requireNonNull(value, "credit id must not be null");
  }
  
  public static CreditId newId() {
    return new CreditId(ID_GENERATOR.generate());
  }
  
  public static CreditId of(UUID value) {
    return new CreditId(value);
  }
}
