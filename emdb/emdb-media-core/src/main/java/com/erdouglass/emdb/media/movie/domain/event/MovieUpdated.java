package com.erdouglass.emdb.media.movie.domain.event;

import java.util.Objects;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Title;

public record MovieUpdated(PublicId id, TmdbId tmdbId, Title title) implements MovieEvent {

  public MovieUpdated {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(title, "title is required");
  }
  
  public static MovieUpdated of(PublicId id, TmdbId tmdbId, Title title) {
    return new MovieUpdated(id, tmdbId, title);
  }
}
