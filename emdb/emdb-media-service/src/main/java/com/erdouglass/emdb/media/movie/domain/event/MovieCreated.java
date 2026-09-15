package com.erdouglass.emdb.media.movie.domain.event;

import java.util.Objects;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbId;

public record MovieCreated(
    PublicId id, 
    TmdbId tmdbId, 
    Title title, 
    DateTime createdAt) implements DomainEvent {
  
  public MovieCreated {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(title, "title is required");
    Objects.requireNonNull(createdAt, "createdAt is required");
  }
  
  public static MovieCreated of(PublicId id, TmdbId tmdbId, Title title) {
    return new MovieCreated(id, tmdbId, title, DateTimeFactory.now());
  }
}
