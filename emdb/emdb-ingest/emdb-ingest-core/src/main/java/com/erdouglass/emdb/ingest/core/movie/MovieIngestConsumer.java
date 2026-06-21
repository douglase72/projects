package com.erdouglass.emdb.ingest.core.movie;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.core.Log;
import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.command.MovieCommandService;
import com.erdouglass.emdb.media.command.SaveMovie;
import com.erdouglass.emdb.media.query.MovieDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;

@ApplicationScoped
public class MovieIngestConsumer {
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  
  @Inject
  ObjectMapper mapper;
  
  @ConfigProperty(name = "emdb.movie.data")
  String data;
  
  @Inject
  TmdbMovieScraper scraper;
  
  @Inject
  MovieCommandService service;
  
  @Log
  public MovieDto ingest(Message<IngestMedia> message) throws IOException {
    var payload = message.getPayload();
    var command = scraper.scrape(payload.tmdbId());
    
    try {
      return service.save(command);
    } catch (ConstraintViolationException e) {
      var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
          .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
      var fn = metadata.getCorrelationId()
          .map(id -> id + ".json")
          .orElseThrow();
      var cmd = SaveMovie.builder(command)
          .backdrop(saveImage(command.backdrop()))
          .poster(saveImage(command.poster()))
          .build();
      var file = Path.of(data, fn).toFile();
      mapper.writerWithDefaultPrettyPrinter().writeValue(file, cmd);
      var perms = PosixFilePermissions.fromString("rw-rw-rw-");
      Files.setPosixFilePermissions(file.toPath(), perms);
      throw e;
    }
  }
  
  private Image saveImage(Image image) throws IOException {
    if (image == null || image.data() == null) {
      return image;
    }
    var fn = GENERATOR.generate();
    Files.write(Path.of(data, fn.toString()), image.data());
    return new Image(fn, null);
  }  
}
