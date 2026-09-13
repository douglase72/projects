package com.erdouglass.emdb.ingest.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.ingest.application.port.out.MovieRepository;
import com.erdouglass.emdb.ingest.domain.model.IngestId;
import com.erdouglass.emdb.media.SaveMovieCommand;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
class MovieRepositoryAdapter implements MovieRepository {
  private static final Logger LOGGER = Logger.getLogger(MovieRepositoryAdapter.class);
  
  @Inject
  @Channel("ingest-movie-out")
  Emitter<SaveMovieCommand> emitter;
  
  @Inject
  MovieMapper mapper;

  @Override
  public void save(IngestId id, Movie movie) {
    var command = mapper.toSaveMovieCommand(movie);
    LOGGER.infof("command: %s", command);
    emitter.send(Message.of(command).addMetadata(OutgoingRabbitMQMetadata.builder()
        .withCorrelationId(id.value().toString())
        .build()));
  }
}
