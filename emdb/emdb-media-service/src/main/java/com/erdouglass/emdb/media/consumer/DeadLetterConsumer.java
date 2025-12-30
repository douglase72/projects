package com.erdouglass.emdb.media.consumer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
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

import io.opentelemetry.api.baggage.Baggage;

@ApplicationScoped
public class DeadLetterConsumer {
  private static final DateTimeFormatter FILE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
  private static final Logger LOGGER = Logger.getLogger(DeadLetterConsumer.class);
  
  @ConfigProperty(name = "emdb.cli.movies")
  String dest;
  
  @Inject
  ObjectMapper objectMapper;
  
  @Incoming("movie-dlq-in")
  public void onMessage(MovieCreateMessage message) {
    var jobId = Baggage.current().getEntryValue("job-id"); 
    LOGGER.infof("DLQ Received: %s for TMDB movie %d", jobId, message.tmdbId());
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
