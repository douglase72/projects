package com.erdouglass.emdb.media.movie.domain.event;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbId;

public sealed interface DomainEvent permits MovieCreated, MovieUpdated {

  PublicId id(); 
  
  TmdbId tmdbId();
  
  Title title();
  
  DateTime createdAt();
}
