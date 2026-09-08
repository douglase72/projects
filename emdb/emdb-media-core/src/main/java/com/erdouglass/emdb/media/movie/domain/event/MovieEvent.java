package com.erdouglass.emdb.media.movie.domain.event;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Title;

public sealed interface MovieEvent permits MovieCreated, MovieUpdated {

  PublicId id(); 
  TmdbId tmdbId();
  Title title();
}
