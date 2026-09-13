package com.erdouglass.emdb.media.person.adapter.in.messaging;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SavePersonCommand;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonUseCase;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
class PersonConsumer {
  private static final Logger LOGGER = Logger.getLogger(PersonConsumer.class);
  
  @Inject
  SavePersonUseCase saveUseCase;
  
  @RunOnVirtualThread
  @Incoming("ingest-person-in")
  public CompletionStage<Void> onMessage(Message<SavePersonCommand> message) {
    try {
      var command = message.getPayload();
      saveUseCase.save(command);
      return message.ack();
    } catch (Exception e) {
      LOGGER.errorf(e , "Failed to save person");
      return message.nack(e);
    }
  }
}
