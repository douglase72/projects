package com.erdouglass.emdb.media.movie.application.port.out;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.TmdbId;

public record PersonCredit(TmdbId tmdbId, Name name) {

  public PersonCredit {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(name, "name id is required");
  }
  
  public static PersonCredit of(TmdbId tmdbId, Name name) {
    return new PersonCredit(tmdbId, name);
  }
}
