package com.erdouglass.emdb.scraper.application.port.out;

import com.erdouglass.emdb.ingest.messaging.IngestEvent;

public interface IngestEventPublisher {
  
  void publish(IngestEvent event);
}
