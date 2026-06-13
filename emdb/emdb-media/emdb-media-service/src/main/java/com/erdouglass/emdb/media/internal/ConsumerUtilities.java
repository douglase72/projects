package com.erdouglass.emdb.media.internal;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.command.IngestMedia;
import com.erdouglass.emdb.media.command.SaveCommand;

import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;

@ApplicationScoped
public class ConsumerUtilities {
  
  @Inject 
  Validator validator;
  
  public long elapsedTime(Message<?> message) {
    return message.getMetadata(IncomingRabbitMQMetadata.class)
        .map(m -> m.getHeaders().get(IngestMedia.START_TIME))
        .map(Object::toString)
        .map(Instant::parse)
        .map(start -> Duration.between(start, Instant.now()).toMillis())
        .orElse(0L);
  }  
  
  public void validate(SaveCommand command) {
    var violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
