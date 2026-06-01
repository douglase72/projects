package com.erdouglass.emdb.media.messaging;

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

import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.person.SavePerson;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.common.annotation.RunOnVirtualThread;

/// Consumes [SavePerson] commands that were rejected by [PersonConsumer] and
/// routed to the person dead-letter queue. Each failed command is serialized
/// to a JSON file on disk so it can be inspected, corrected, and replayed.
@ApplicationScoped
public class PersonDeadLetterConsumer {
  private static final Logger LOGGER = Logger.getLogger(PersonDeadLetterConsumer.class);
  private static final DateTimeFormatter FILE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
  
  @Inject
  ObjectMapper objectMapper;
  
  @ConfigProperty(name = "emdb.person.data")
  String personPath;
  
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
  @Incoming("person-dlq-in")
  public void onMessage(SavePerson command) {
    var name = command.name().replace(":", "").replace(" ", "-");
    var ts = LocalDateTime.now().format(FILE_FMT);
    var base = String.format("%s-%s", name, ts);
    var jsonPath = Path.of(personPath, base + ".json");
    
    try {
      var cmd = SavePerson.builder(command)
          .profile(saveImage(command.profile(), base + ".profile"))
          .build();
      var file = jsonPath.toFile();
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, cmd); 
      var perms = PosixFilePermissions.fromString("rw-rw-rw-");
      Files.setPosixFilePermissions(file.toPath(), perms);
      LOGGER.infof("Created %s for inspection", file.getAbsolutePath());
    } catch (IOException e) {
      LOGGER.errorf(e, "Failed to write file to: %s", jsonPath);
    }    
  }
  
  private Image saveImage(final Image image, final String fileName) throws IOException {
    if (image == null || image.data() == null) {
      return image;
    }
    Files.write(Path.of(personPath, fileName), image.data());
    return Image.builder()
        .tmdbName(image.tmdbName())
        .build();
  }
}
