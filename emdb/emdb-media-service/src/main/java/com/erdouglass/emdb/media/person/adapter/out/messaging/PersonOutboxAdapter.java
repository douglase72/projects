package com.erdouglass.emdb.media.person.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.IngestMediaCommand;
import com.erdouglass.emdb.ingest.IngestMediaCommand.IngestType;
import com.erdouglass.emdb.media.person.adapter.out.persistence.JakartaDataPersonOutboxRepository;
import com.erdouglass.emdb.media.person.domain.event.PersonCreated;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.Scheduled.ConcurrentExecution;

@ApplicationScoped
class PersonOutboxAdapter {
  
  @Inject
  @Channel("ingest-media-out")
  Emitter<IngestMediaCommand> emitter;
  
  @Inject
  JakartaDataPersonOutboxRepository repository;

  @Scheduled(
      every = "{emdb.media.outbox.interval}", 
      delayed = "{emdb.media.outbox.delay}",
      concurrentExecution = ConcurrentExecution.SKIP)
  void execute() {
    publish();
  }
  
  void onCreated(@Observes(during = TransactionPhase.AFTER_SUCCESS) PersonCreated event) {
    publish();
  }
  
  private void publish() {
    var people = repository.findAll();
    for (var person : people) {
      var command = IngestMediaCommand.of(person.getTmdbId(), IngestType.PERSON);
      emitter.send(Message.of(command));
    }
    repository.deleteAll(people);
  }
}
