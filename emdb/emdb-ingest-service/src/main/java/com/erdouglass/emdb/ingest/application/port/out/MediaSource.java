package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.IngestMediaCommand.IngestType;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

public interface MediaSource {

  Media extract(TmdbId tmdbId, IngestType type);
}
