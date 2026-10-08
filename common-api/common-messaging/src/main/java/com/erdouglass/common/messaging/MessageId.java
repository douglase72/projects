package com.erdouglass.common.messaging;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

public record MessageId(UUID value) {
  private static final TimeBasedEpochGenerator ID_GENERATOR = Generators.timeBasedEpochGenerator();
  
  public MessageId {
    Objects.requireNonNull(value, "id is required");
  }
  
  public static MessageId newId() { return new MessageId(ID_GENERATOR.generate()); }
  public static MessageId of(UUID id) { return new MessageId(id); }
}
