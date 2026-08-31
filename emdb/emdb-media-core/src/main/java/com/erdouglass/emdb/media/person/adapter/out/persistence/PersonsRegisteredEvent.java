package com.erdouglass.emdb.media.person.adapter.out.persistence;

import java.util.List;
import java.util.Objects;

public record PersonsRegisteredEvent(List<Integer> tmdbIds) { 
  
  public PersonsRegisteredEvent {
    Objects.requireNonNull(tmdbIds, "TMDB ids are required");
  }
  
  public static PersonsRegisteredEvent of(List<Integer> tmdbIds) {
    return new PersonsRegisteredEvent(tmdbIds);
  }
}
