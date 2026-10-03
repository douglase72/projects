package com.erdouglass.emdb.ingest.domain.event;

import java.time.Instant;

import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public sealed interface IngestEvent permits IngestSubmitted, IngestStarted, IngestExtracted {
 
  IngestId ingestId();
  
  MediaType mediaType();
  
  Instant occurredAt();
  
  TmdbId tmdbId();
}
