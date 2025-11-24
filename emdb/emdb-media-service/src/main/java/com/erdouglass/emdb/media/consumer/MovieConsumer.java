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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.query.MovieStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageType;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.common.annotation.RunOnVirtualThread;

/// Consumes movie commands to persist data or handle failures.
///
/// This consumer listens on two channels:
/// 1. `movie-create-in`: For valid commands to be persisted.
/// 2. `dead-letters`: For invalid commands to be dumped to disk.
@ApplicationScoped
public class MovieConsumer {
  private static final DateTimeFormatter FILE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @ConfigProperty(name = "emdb.cli.movies")
  String dest;
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  ObjectMapper objectMapper;

  @Inject
  MovieService service;
  
  @Inject
  @Channel("movie-status-out") 
  Emitter<MovieStatus> statusEmitter;
  
  /// Consumes valid movie creation commands and persists them to the database.
  ///
  /// @param command The validated movie command.
  @RunOnVirtualThread
  @Incoming("movie-create-in")
  public void create(@NotNull @Valid MovieCreateCommand command) {
    LOGGER.infof("Received: %s", command);
    service.create(mapper.toMovie(command)); 
    statusEmitter.send(MovieStatus.of(command.tmdbId(), MessageType.INGEST, MessageStatus.LOADED));
  }
  
  /// Handles messages that have failed processing or validation.
  ///
  /// This method:
  /// 1. Sends a `FAILED` status update.
  /// 2. Serializes the failed command to a JSON file on disk for inspection.
  ///
  /// @param command The failed movie command.
  @RunOnVirtualThread
  @Incoming("dead-letters")
  public void failed(@NotNull MovieCreateCommand command) {
    statusEmitter.send(MovieStatus.of(command.tmdbId(), MessageType.INGEST, MessageStatus.FAILED));
    var title = command.title().replace(" ", "-");
    var ts = LocalDateTime.now().format(FILE_FMT);
    var file = Path.of(dest, String.format("%s-%s.json", title, ts)).toFile();
    
    try {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, command);
      Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-rw-rw-");
      Files.setPosixFilePermissions(file.toPath(), perms);
    } catch (IOException e) {
      LOGGER.errorf(e, "Failed to write command to: %s", file.getAbsolutePath());
    }
  }

}
