package com.erdouglass.emdb.media.api;

import java.util.Objects;
import java.util.UUID;

public record EventId(UUID value) {

  public EventId {
    Objects.requireNonNull(value, "event id must not be null");
  }
  
  public static EventId newId() { return new EventId(UUID.randomUUID()); }
  public static EventId of(UUID id) { return new EventId(id); }
}
