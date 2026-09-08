package com.erdouglass.emdb.media.person.domain.event;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.person.domain.model.Name;

public sealed interface PersonEvent permits PersonCreated, PersonUpdated {

  PublicId id(); 
  TmdbId tmdbId();
  Name name();
}
