package com.erdouglass.emdb.ingest.domain.event;

import java.time.Instant;

import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.media.TmdbId;

public sealed interface IngestEvent permits IngestSubmittedEvent, IngestStartedEvent,   IngestExtractedEvent,
                                            IngestLoadedEvent,    IngestCompletedEvent, IngestFailedEvent {
  
  IngestId id();
  
  String message();
  
  Instant occurredAt();
    
  TmdbId tmdbId();
}
