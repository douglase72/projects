package com.erdouglass.emdb.media.movie.domain.event;

import java.util.Objects;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Title;

public record MovieCreated(PublicId id, TmdbId tmdbId, Title title) implements MovieEvent {

  public MovieCreated {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(title, "title is required");
  }
  
  public static MovieCreated of(PublicId id, TmdbId tmdbId, Title title) {
    return new MovieCreated(id, tmdbId, title);
  }
}
