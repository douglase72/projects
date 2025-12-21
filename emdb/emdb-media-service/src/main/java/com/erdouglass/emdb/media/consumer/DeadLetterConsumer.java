package com.erdouglass.emdb.media.consumer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.message.MovieCreateMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class DeadLetterConsumer {
  private static final DateTimeFormatter FILE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
  private static final Logger LOGGER = Logger.getLogger(DeadLetterConsumer.class);
  
  @ConfigProperty(name = "emdb.cli.movies")
  String dest;
  
  @Inject
  ObjectMapper objectMapper;
  
  @RunOnVirtualThread
  @Incoming("dead-letters-in")
  public void onMessage(MovieCreateMessage message) {
    var latency = Duration.between(message.timestamp(), Instant.now()).toMillis();
    LOGGER.infof("Message: %s, latency: %d ms", message, latency);
    var title = message.title().replace(":", "").replace(" ", "-");
    var ts = LocalDateTime.now().format(FILE_FMT);
    var file = Path.of(dest, String.format("%s-%s.json", title, ts)).toFile();

    try {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, message);
      Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-rw-rw-");
      Files.setPosixFilePermissions(file.toPath(), perms);
      LOGGER.infof("Created %s for manual ingestion", file.getAbsolutePath());
    } catch (IOException e) {
      LOGGER.errorf(e, "Failed to write file to: %s", file.getAbsolutePath());
    }    
  }
  
}
