package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.domain.model.Ingest;

public interface IngestPublisher {

  void publish(Ingest job);
}
