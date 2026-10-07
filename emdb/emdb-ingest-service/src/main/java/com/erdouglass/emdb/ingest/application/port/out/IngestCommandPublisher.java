package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.messaging.IngestCommand;

public interface IngestCommandPublisher {

  void publish(IngestCommand command);
}
