package com.erdouglass.emdb.media.messaging;

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

import com.erdouglass.emdb.common.comand.SaveMovie;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class MovieDlqConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieDlqConsumer.class);
  private static final DateTimeFormatter FILE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
  
  @Inject
  ObjectMapper objectMapper;
  
  @ConfigProperty(name = "emdb.movie.data")
  String moviePath;
  
  @RunOnVirtualThread
  @Incoming("movie-dlq-in")
  public void onMessage(SaveMovie command) {
    var title = command.title().replace(":", "").replace(" ", "-");
    var ts = LocalDateTime.now().format(FILE_FMT);
    var file = Path.of(moviePath, String.format("%s-%s.json", title, ts)).toFile();
    
    try {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, command);
      Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-rw-rw-");
      Files.setPosixFilePermissions(file.toPath(), perms);
      LOGGER.infof("Created %s for manual ingestion", file.getAbsolutePath());
    } catch (IOException e) {
      LOGGER.errorf(e, "Failed to write file to: %s", file.getAbsolutePath());
    }
  }
  
}
