package com.erdouglass.emdb.media.person.adapter.out.event;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.api.PersonStubCreated;
import com.erdouglass.emdb.media.person.application.port.out.PersonEventPublisher;

@ApplicationScoped
class PersonEventAdapter implements PersonEventPublisher {
  
  @Inject
  Event<PersonStubCreated> emitter;

  @Override
  public void publish(List<PersonStubCreated> events) {
    events.forEach(emitter::fire);
  }
}
