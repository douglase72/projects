package com.erdouglass.emdb.ingest.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.out.Person;
import com.erdouglass.emdb.ingest.application.port.out.PersonRepository;
import com.erdouglass.emdb.media.SavePersonCommand;

@ApplicationScoped
class PersonRepositoryAdapter implements PersonRepository {
  private static final Logger LOGGER = Logger.getLogger(PersonRepositoryAdapter.class);
  
  @Inject
  @Channel("ingest-person-out")
  Emitter<SavePersonCommand> emitter;
  
  @Inject
  PersonMapper mapper;

  @Override
  public void save( Person person) {
    var command = mapper.toSavePersonCommand(person);
    
    try {
      emitter.send(Message.of(command));
    }  catch (Exception e) {
      LOGGER.errorf(e, "Failed to publish command: %s", command);
      throw e;
    }
  }
}
