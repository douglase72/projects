package com.erdouglass.emdb.ingest.application.port.in;

import com.erdouglass.emdb.ingest.domain.event.IngestEvent;

public interface SaveIngestEventUseCase {

  void save(IngestEvent event);
}
