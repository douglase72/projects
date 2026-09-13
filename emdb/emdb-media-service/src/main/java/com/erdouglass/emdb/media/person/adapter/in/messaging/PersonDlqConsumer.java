package com.erdouglass.emdb.media.person.adapter.in.messaging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
class PersonDlqConsumer {
  private static final Logger LOGGER = Logger.getLogger(PersonDlqConsumer.class);
  
  @ConfigProperty(name = "emdb.people.data")
  Path directory;
  
  @RunOnVirtualThread
  @Incoming("ingest-person-dlq-in")
  public CompletionStage<Void> onMessage(Message<JsonObject> message) {
    try {
      var file = park(message);
      LOGGER.infof("Saved: %s", file);
      return message.ack();
    } catch (IOException e) {
      LOGGER.errorf(e, "Failed to write file.");
      return message.nack(e);
    }
  }
  
  private Path park(Message<JsonObject> message) throws IOException {
    var metadata = message.getMetadata(IncomingRabbitMQMetadata.class);
    var correlationId = metadata
        .flatMap(IncomingRabbitMQMetadata::getCorrelationId)
        .orElse("unknown");
    Files.createDirectories(directory);        
    var file = directory.resolve("SavePersonCommand-%s.json".formatted(correlationId));
    Files.writeString(file, message.getPayload().encodePrettily(), StandardCharsets.UTF_8);
    return file;
  }
}
