package com.erdouglass.emdb.media.person.domain.event;

import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.person.domain.model.Name;

public record PersonCreated(PublicId id, TmdbId tmdbId, Name name, DateTime createdAt) implements DomainEvent {
  
  public PersonCreated {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(name, "name is required");    
  }
  
  public static PersonCreated of(PublicId id, TmdbId tmdbId, Name name) {
    return new PersonCreated(id, tmdbId, name, DateTimeFactory.now());
  }
}
