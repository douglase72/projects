package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.domain.event.IngestEvent;

public interface IngestEventPublisher {

  void publish(IngestEvent event);
}
