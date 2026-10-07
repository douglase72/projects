package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.domain.event.DomainEvent;

public interface IngestEventPublisher {

  void publish(DomainEvent event);
}
