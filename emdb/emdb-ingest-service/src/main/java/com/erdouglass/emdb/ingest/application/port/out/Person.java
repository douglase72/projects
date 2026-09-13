package com.erdouglass.emdb.ingest.application.port.out;

import java.util.Objects;

import lombok.Builder;

@Builder
public record Person(
    Integer tmdbId,
    String name,
    String birthDate,
    String deathDate,
    String gender,
    String biography) {
  
  public Person {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(name, "name must not be null");
  } 
}
