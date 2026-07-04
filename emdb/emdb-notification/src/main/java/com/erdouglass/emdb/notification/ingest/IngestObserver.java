package com.erdouglass.emdb.notification.ingest;

import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.IngestCompleted;
import com.erdouglass.emdb.ingest.IngestEvent;
import com.erdouglass.emdb.ingest.IngestFailed;
import com.erdouglass.emdb.ingest.IngestProgressed;
import com.erdouglass.emdb.ingest.IngestStarted;
import com.erdouglass.emdb.ingest.IngestSubmitted;
import com.erdouglass.emdb.media.MediaType;
import com.erdouglass.emdb.notification.ingest.Ingest.Type;

@ApplicationScoped
class IngestObserver {
  private static final Logger LOGGER = Logger.getLogger(IngestObserver.class);
  
  @Inject
  IngestRepository repository;

  @Transactional
  public void onEvent(@Observes IngestEvent event) {
    var ingest = switch (event) {
      case IngestSubmitted e  -> toIngest(e.id(), e.createdAt(), Type.SUBMITTED, e.type(), e.message());
      case IngestStarted e    -> toIngest(e.id(), e.createdAt(), Type.STARTED, e.type(), e.message());
      case IngestProgressed e -> toIngest(e.id(), e.createdAt(), Type.PROGRESSED, e.type(), e.message());
      case IngestCompleted e  -> toIngest(e.id(), e.createdAt(), Type.COMPLETED, e.type(), e.message());
      case IngestFailed e     -> toIngest(e.id(), e.createdAt(), Type.FAILED, e.type(), e.message());
    };
    repository.save(ingest);
    LOGGER.debugf("Saved: %s", ingest);
  }
  
  private Ingest toIngest(UUID id, Instant createdAt, Type type, MediaType mediaType, String message) {
    var ingest = new Ingest();
    ingest.setCorrelationId(id);
    ingest.setCreatedAt(createdAt);
    ingest.setType(type);
    ingest.setMediaType(mediaType);
    ingest.setMessage(message);
    return ingest;
  }
}
