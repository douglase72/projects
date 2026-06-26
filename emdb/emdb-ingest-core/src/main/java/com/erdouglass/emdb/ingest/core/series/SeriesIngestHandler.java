package com.erdouglass.emdb.ingest.core.series;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.core.Log;
import com.erdouglass.emdb.media.IngestMedia;

@ApplicationScoped
public class SeriesIngestHandler {

  @Log
  public void ingest(Message<IngestMedia> message) {
    throw new UnsupportedOperationException();
  }
}
