package com.erdouglass.emdb.ingest.application.port.out;

import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

import lombok.Builder;

@Builder
public record Person(
    TmdbId tmdbId,
    String name,
    DateTime birthDate,
    DateTime deathDate,
    String gender,
    String biography) {

  public Person {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(name, "name must not be null");
  }  
}
