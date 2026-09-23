package com.erdouglass.emdb.ingest.domain.event;

import com.erdouglass.common.util.DateTime;
import com.erdouglass.emdb.ingest.domain.model.IngestId;

public sealed interface IngestEvent permits IngestSubmitted, IngestStarted, IngestExtracted, 
                                            IngestFailed {

  IngestId id();
  
  DateTime occurredAt();
  
  String message();
}
