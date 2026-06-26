package com.erdouglass.emdb.ingest.core.person;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.core.Log;
import com.erdouglass.emdb.media.IngestMedia;

@ApplicationScoped
public class PersonIngestHandler {

  @Log
  public void ingest(Message<IngestMedia> message) {
    throw new UnsupportedOperationException();
  }
}
