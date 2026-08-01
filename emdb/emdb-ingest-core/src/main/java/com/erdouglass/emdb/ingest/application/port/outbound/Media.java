package com.erdouglass.emdb.ingest.application.port.outbound;

import com.erdouglass.emdb.ingest.domain.model.IngestType;
import com.erdouglass.emdb.media.SourceId;

public sealed interface Media permits Movie {

  SourceId sourceId();
  
  IngestType type();
}
