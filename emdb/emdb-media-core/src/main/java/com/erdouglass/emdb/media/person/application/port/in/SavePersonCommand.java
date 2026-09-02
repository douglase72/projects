package com.erdouglass.emdb.media.person.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.person.domain.model.PersonDetails;

public record SavePersonCommand(
    TmdbId tmdbId,
    PersonDetails details) {

  public SavePersonCommand {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(details, "person details are required");
  }
  
  public static SavePersonCommand of(TmdbId tmdbId, PersonDetails details) {
    return new SavePersonCommand(tmdbId, details);
  }
}