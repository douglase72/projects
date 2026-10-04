package com.erdouglass.emdb.scraper.application.port.out;

import com.erdouglass.emdb.ingest.messaging.IngestEventMessage;

public interface EventPublisher {

  void publish(IngestEventMessage message);
}
