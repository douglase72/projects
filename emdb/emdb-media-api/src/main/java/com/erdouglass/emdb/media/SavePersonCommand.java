package com.erdouglass.emdb.media;

import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.emdb.common.TmdbId;

import lombok.Builder;

@Builder
public record SavePersonCommand(
    TmdbId tmdbId,
    String name,
    DateTime birthDate,
    DateTime deathDate,
    String gender,
    String biography) {

  public SavePersonCommand {
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(name, "name is required");
  }
}
