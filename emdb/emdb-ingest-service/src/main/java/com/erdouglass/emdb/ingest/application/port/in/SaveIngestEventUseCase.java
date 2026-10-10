package com.erdouglass.emdb.ingest.application.port.in;

import com.erdouglass.emdb.ingest.domain.event.DomainEvent;

public interface SaveIngestEventUseCase {

  void save(DomainEvent event);
}
