package com.erdouglass.emdb.ingest.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;

import com.erdouglass.emdb.ingest.application.port.out.IngestCommandPublisher;
import com.erdouglass.emdb.ingest.messaging.IngestCommand;

import io.smallrye.reactive.messaging.MutinyEmitter;

/// Publishes ingest commands to the broker.
/// 
/// Publish the [IngestCommand] to the ingest-media queue and block until 
/// acknowledged by the RabbitMQ broker.
@ApplicationScoped
class IngestCommandProducer implements IngestCommandPublisher {
  
  @Inject
  @Channel("ingest-media")
  MutinyEmitter<IngestCommand> emitter;

  @Override
  public void publish(IngestCommand command) {
    emitter.sendAndAwait(command);
  }
}
