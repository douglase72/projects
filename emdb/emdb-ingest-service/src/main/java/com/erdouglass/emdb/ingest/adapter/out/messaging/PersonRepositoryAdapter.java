package com.erdouglass.emdb.ingest.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.out.Person;
import com.erdouglass.emdb.ingest.application.port.out.PersonRepository;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.media.SavePersonCommand;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
class PersonRepositoryAdapter implements PersonRepository {
  private static final Logger LOGGER = Logger.getLogger(PersonRepositoryAdapter.class);
  
  @Inject
  @Channel("ingest-person-out")
  Emitter<SavePersonCommand> emitter;
  
  @Inject
  PersonMapper mapper;

  @Override
  public void save(IngestId id, Person person) {
    var command = mapper.toSavePersonCommand(person);
    LOGGER.infof("command: %s", command);
    emitter.send(Message.of(command).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withCorrelationId(id.value().toString())
        .build()));
  }
}
