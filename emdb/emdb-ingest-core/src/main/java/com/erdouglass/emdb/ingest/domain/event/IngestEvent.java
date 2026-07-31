package com.erdouglass.emdb.ingest.domain.event;

import java.time.Instant;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public sealed interface IngestEvent permits IngestSubmittedEvent, IngestStartedEvent, IngestExtractedEvent, 
                                            IngestCompletedEvent, IngestLoadedEvent,  IngestFailedEvent {

  IngestId id();
  
  Instant occurredAt();
  
  String message();
}
