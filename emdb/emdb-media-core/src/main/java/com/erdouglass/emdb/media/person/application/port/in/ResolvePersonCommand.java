package com.erdouglass.emdb.media.person.application.port.in;

import java.util.Objects;
import java.util.Set;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.Name;

public record ResolvePersonCommand(Set<Reference> references) {

  public ResolvePersonCommand {
    references = Set.copyOf(references);
  }
  
  public static ResolvePersonCommand of(Set<Reference> references) {
    Objects.requireNonNull(references, "references are required");
    return new ResolvePersonCommand(references);
  }

  public record Reference(TmdbId tmdbId, Name name) {
    
    public Reference {
      Objects.requireNonNull(tmdbId, "TMDB id is required");
      Objects.requireNonNull(name, "name id is required");
    }
    
    public static Reference of(TmdbId tmdbId, Name name) {
      return new Reference(tmdbId, name);
    }
  }
}
