package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.messaging.IngestMediaMessage;

public interface IngestCommandPublisher {

  void publish(IngestMediaMessage message);
}
