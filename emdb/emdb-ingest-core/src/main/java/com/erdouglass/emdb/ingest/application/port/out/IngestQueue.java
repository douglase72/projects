package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface IngestQueue {
  
  void publish(IngestId id);
}
