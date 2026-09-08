package com.erdouglass.emdb.media.person.domain.event;

import java.util.Objects;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.person.domain.model.Name;

public record PersonCreated(PublicId id, TmdbId tmdbId, Name name) implements PersonEvent {

  public PersonCreated {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(name, "name is required");    
  }
  
  public static PersonCreated of(PublicId id, TmdbId tmdbId, Name name) {
    return new PersonCreated(id, tmdbId, name);
  }
}
