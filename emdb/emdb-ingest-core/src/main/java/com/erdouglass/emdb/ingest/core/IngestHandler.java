package com.erdouglass.emdb.ingest.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.media.IngestMedia;
import com.erdouglass.emdb.media.image.Image;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;

public abstract class IngestHandler<C, R> {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  
  @Inject
  ObjectMapper mapper;
  
  public abstract R ingest(Message<IngestMedia> message) throws IOException;
    
  protected abstract String mediaPath();
  
  protected Image saveImage(Image image) throws IOException {
    if (image == null || image.data() == null) {
      return image;
    }
    var fn = GENERATOR.generate();
    Files.write(Path.of(mediaPath(), fn.toString()), image.data());
    return new Image(fn, null);
  }
  
  protected void saveMessage(Message<IngestMedia> message, C command) throws IOException {
    var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
        .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));    
    var fn = metadata.getCorrelationId()
        .map(id -> id + ".json")
        .orElseThrow();
    var file = Path.of(mediaPath(), fn).toFile();
    mapper.writerWithDefaultPrettyPrinter().writeValue(file, command);
    var perms = PosixFilePermissions.fromString("rw-rw-rw-");
    Files.setPosixFilePermissions(file.toPath(), perms);
  }
}
