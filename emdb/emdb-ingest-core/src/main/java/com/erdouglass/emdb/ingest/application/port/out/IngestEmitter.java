package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.domain.model.IngestId;

public interface IngestEmitter {

  void publish(IngestId id);
}
