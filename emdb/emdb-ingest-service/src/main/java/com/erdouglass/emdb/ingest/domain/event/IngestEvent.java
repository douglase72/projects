package com.erdouglass.emdb.ingest.domain.event;

import com.erdouglass.emdb.ingest.IngestMediaCommand.IngestType;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.ingest.domain.model.TmdbId;

public sealed interface IngestEvent permits IngestSubmitted, IngestStarted, IngestExtracted, IngestFailed {

  IngestId id();
  
  TmdbId tmdbId();
  
  IngestType type();
}
