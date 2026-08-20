package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.application.dto.Movie;
import com.erdouglass.emdb.media.TmdbId;

public interface MovieSource {

  Movie extract(TmdbId tmdbId);
}
