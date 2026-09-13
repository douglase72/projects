package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.IngestMediaCommand;

public interface IngestPublisher {

  void publish(IngestMediaCommand command);
}
