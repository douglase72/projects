package com.erdouglass.emdb.media.consumer;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.query.MovieStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageType;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class MovieConsumer {
  private static final DateTimeFormatter FILE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
  
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
  
  @RunOnVirtualThread
  @Incoming("movie-create-in")
  public void create(@NotNull @Valid MovieCreateCommand command) {
    service.create(mapper.toMovie(command)); 
    statusEmitter.send(MovieStatus.of(command.tmdbId(), MessageType.INGEST, MessageStatus.LOADED));
  }
  
  @RunOnVirtualThread
  @Incoming("dead-letters")
  public void failed(@NotNull MovieCreateCommand command) throws IOException {
    statusEmitter.send(MovieStatus.of(command.tmdbId(), MessageType.INGEST, MessageStatus.FAILED));
    var title = command.title().replace(" ", "-");
    var ts = LocalDateTime.now().format(FILE_FMT);
    var file = Path.of(dest, String.format("%s-%s.json", title, ts)).toFile();
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, command);
  }

}
