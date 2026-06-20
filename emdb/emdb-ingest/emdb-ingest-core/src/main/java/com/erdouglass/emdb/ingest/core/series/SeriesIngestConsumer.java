package com.erdouglass.emdb.ingest.core.series;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.core.Log;

@ApplicationScoped
public class SeriesIngestConsumer {

  @Log
  public void ingest(Message<IngestMedia> message) {
    throw new UnsupportedOperationException();
  }
}
