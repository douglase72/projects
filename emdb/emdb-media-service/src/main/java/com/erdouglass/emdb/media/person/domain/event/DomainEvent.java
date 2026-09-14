package com.erdouglass.emdb.media.person.domain.event;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.person.domain.model.Name;

public interface DomainEvent {
  
  PublicId id(); 
  
  TmdbId tmdbId();
  
  Name name();
  
  DateTime createdAt();
}
