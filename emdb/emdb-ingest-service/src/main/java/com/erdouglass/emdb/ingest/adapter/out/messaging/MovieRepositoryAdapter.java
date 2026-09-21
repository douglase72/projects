package com.erdouglass.emdb.ingest.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.ingest.application.port.out.MovieRepository;
import com.erdouglass.emdb.media.SaveMovieCommand;

@ApplicationScoped
class MovieRepositoryAdapter implements MovieRepository {
  private static final Logger LOGGER = Logger.getLogger(MovieRepositoryAdapter.class);
  
  @Inject
  @Channel("ingest-movie-out")
  Emitter<SaveMovieCommand> emitter;
  
  @Inject
  MovieMapper mapper;

  @Override
  public void save(Movie movie) {
    var command = mapper.toSaveMovieCommand(movie);
    
    try {
      emitter.send(Message.of(command));
    } catch (Exception e) {
      LOGGER.errorf(e, "Failed to publish command: %s", command);
      throw e;
    }
  }
}
