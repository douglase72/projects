package com.erdouglass.emdb.media.movie;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.SaveMovie;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.common.annotation.RunOnVirtualThread;

/// Consumes [SaveMovie] commands that were rejected by [MovieConsumer] and
/// routed to the movie dead-letter queue. Each failed command is serialized
/// to a JSON file on disk so it can be inspected, corrected, and replayed.
@ApplicationScoped
class MovieDeadLetterConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieDeadLetterConsumer.class);
  private static final DateTimeFormatter FILE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
  
  @Inject
  ObjectMapper objectMapper;
  
  @ConfigProperty(name = "emdb.movie.data")
  String moviePath;
  
  /// Writes the rejected command to a JSON file under `emdb.movie.data`.
  ///
  /// The filename is composed of the movie's title (sanitized of spaces and
  /// colons), release year, and a timestamp, so repeated failures of the same
  /// movie produce distinct files rather than overwriting each other. The
  /// file is given world-readable/writable POSIX permissions so it can be
  /// edited by an operator without root access.
  ///
  /// I/O failures are logged but not rethrown — the message is implicitly
  /// acknowledged so it does not bounce back into the dead-letter queue.
  ///
  /// @param command the rejected save-movie command to persist for inspection  
  @RunOnVirtualThread
  @Incoming("movie-dlq-in")
  public void onMessage(SaveMovie command) {
    var title = command.title().replace(":", "").replace(" ", "-");
    var year = command.releaseDate().getYear();
    var ts = LocalDateTime.now().format(FILE_FMT);
    var path = Path.of(moviePath, String.format("%s-%04d-%s.json", title, year, ts));
    
    try {
      var file = path.toFile();
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, command);
      var perms = PosixFilePermissions.fromString("rw-rw-rw-");
      Files.setPosixFilePermissions(file.toPath(), perms);
      LOGGER.infof("Created %s for inspection", file.getAbsolutePath());
    } catch (IOException e) {
      LOGGER.errorf(e, "Failed to write file to: %s", path);
    }
  } 
}