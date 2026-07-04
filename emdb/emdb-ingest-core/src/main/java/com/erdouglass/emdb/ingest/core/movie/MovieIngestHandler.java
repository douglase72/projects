package com.erdouglass.emdb.ingest.core.movie;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.core.IngestHandler;
import com.erdouglass.emdb.ingest.logging.Log;
import com.erdouglass.emdb.media.movie.MovieDto;
import com.erdouglass.emdb.media.movie.MovieCommandService;
import com.erdouglass.emdb.media.movie.SaveMovie;

@ApplicationScoped
public class MovieIngestHandler extends IngestHandler<SaveMovie, MovieDto> {
  
  @ConfigProperty(name = "emdb.movie.data")
  String path;
  
  @Inject
  MovieScraper scraper;
  
  @Inject
  MovieCommandService service;
  
  @Log
  @Override
  public MovieDto ingest(Message<IngestMedia> message) throws IOException {
    var command = scraper.scrape(message);
    try {
      return service.save(command);
    } catch (ConstraintViolationException e) {
      var cmd = SaveMovie.builder(command)
          .backdrop(saveImage(command.backdrop()))
          .poster(saveImage(command.poster()))
          .build();
      saveMessage(message, cmd);
      throw e;
    }
  }

  @Override
  protected String mediaPath() {
    return path;
  }  
}
