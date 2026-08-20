package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.application.dto.Person;
import com.erdouglass.emdb.media.TmdbId;

public interface PersonSource {
  
  Person extract(TmdbId tmdbId);
}
