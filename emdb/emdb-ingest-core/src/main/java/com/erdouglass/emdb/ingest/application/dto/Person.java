package com.erdouglass.emdb.ingest.application.dto;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.TmdbId;

import lombok.Builder;

@Builder
public record Person(
    TmdbId tmdbId,
    String name,
    Optional<String> birthDate,
    Optional<String> deathDate,
    Optional<String> gender,
    Optional<String> biography) {

  public Person {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(name, "name must not be null");
  }
}
