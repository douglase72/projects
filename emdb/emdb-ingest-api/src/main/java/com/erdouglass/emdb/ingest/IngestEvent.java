package com.erdouglass.emdb.ingest;

import java.time.Instant;
import java.util.UUID;

import com.erdouglass.emdb.media.MediaType;

public sealed interface IngestEvent 
    permits IngestSubmitted, IngestStarted, IngestProgressed, IngestCompleted, IngestFailed {

  UUID id();
  
  Instant createdAt();
  
  String message();
  
  MediaType type();
}
