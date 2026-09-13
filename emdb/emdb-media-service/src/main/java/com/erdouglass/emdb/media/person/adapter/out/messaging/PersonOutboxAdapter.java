package com.erdouglass.emdb.media.person.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.IngestMediaCommand;
import com.erdouglass.emdb.ingest.IngestMediaCommand.IngestType;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.Scheduled.ConcurrentExecution;

@ApplicationScoped
class PersonOutboxAdapter {
  
  @Inject
  @Channel("ingest-media-out")
  Emitter<IngestMediaCommand> emitter;

  @Scheduled(
      every = "{emdb.media.outbox.interval}", 
      delayed = "{emdb.media.outbox.delay}",
      concurrentExecution = ConcurrentExecution.SKIP)
  void publish() {
    var command = IngestMediaCommand.of(3, IngestType.PERSON);
    emitter.send(Message.of(command));
  }
}
