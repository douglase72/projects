package com.erdouglass.emdb.ingest;

import org.eclipse.microprofile.reactive.messaging.Message;

public interface Scraper {
  
  void scrape(Message<IngestMedia> message);
}
