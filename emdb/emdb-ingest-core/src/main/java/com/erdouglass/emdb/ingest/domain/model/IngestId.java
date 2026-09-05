package com.erdouglass.emdb.ingest.domain.model;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

public record IngestId(UUID value) {
  private static final TimeBasedEpochGenerator ID_GENERATOR = Generators.timeBasedEpochGenerator();
  
  public IngestId {
    Objects.requireNonNull(value, "ingest id must not be null");
  }
  
  public static IngestId newId() {
    return new IngestId(ID_GENERATOR.generate());
  }
}
