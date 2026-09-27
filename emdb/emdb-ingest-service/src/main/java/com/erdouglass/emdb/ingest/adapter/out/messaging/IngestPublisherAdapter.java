package com.erdouglass.emdb.ingest.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.out.IngestPublisher;
import com.erdouglass.emdb.ingest.domain.model.Ingest;
import com.erdouglass.emdb.ingest.messaging.IngestMediaMessage;

@ApplicationScoped
class IngestPublisherAdapter implements IngestPublisher {
  private static final Logger LOGGER = Logger.getLogger(IngestPublisherAdapter.class);
  
  @Inject
  @Channel("ingest-media")
  Emitter<IngestMediaMessage> emitter;

  /// Publish the message to the RabbitMQ exchange.
  @Override
  public void publish(Ingest job) {
    var message = IngestMediaMessage.of(job.tmdbId().value(), job.mediaType().toString());
    emitter.send(message);
    LOGGER.infof("Published: %s", message);
  }
}
