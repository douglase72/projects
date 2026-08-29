package com.erdouglass.emdb.media.person.application.port.out;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.person.domain.model.Name;

public record PersonStub(TmdbId tmdbId, Name name) {

  public PersonStub {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(name, "name id is required");
  }
  
  public static PersonStub of(TmdbId tmdbId, Name name) {
    return new PersonStub(tmdbId, name);
  }
}
