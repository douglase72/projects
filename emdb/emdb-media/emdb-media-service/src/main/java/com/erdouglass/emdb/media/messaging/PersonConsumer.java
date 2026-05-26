package com.erdouglass.emdb.media.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.media.api.command.SavePerson;
import com.erdouglass.emdb.media.domain.PersonService;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

/// Consumes [SavePerson] commands from the RabbitMQ save-person queue.
@ApplicationScoped
public class PersonConsumer extends Consumer<SavePerson> {
  
  @Inject
  PersonService service;
  
  @Override
  @RunOnVirtualThread
  @Incoming("save-person-in")
  public Uni<Void> onMessage(Message<SavePerson> message) { 
    return consume(message);
  }

  @Override
  protected void save(final SavePerson command) {
    service.save(command);
  }
}
