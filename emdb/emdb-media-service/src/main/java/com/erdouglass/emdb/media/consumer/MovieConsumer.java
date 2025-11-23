package com.erdouglass.emdb.media.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.common.query.MovieStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageStatus;
import com.erdouglass.emdb.common.query.MovieStatus.MessageType;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  @Channel("movie-status-out") 
  Emitter<MovieStatus> statusEmitter;
  
  @RunOnVirtualThread
  @Incoming("movie-create-in")
  public void create(JsonObject jsonObject) {
    MovieCreateCommand command = jsonObject.mapTo(MovieCreateCommand.class);
    LOGGER.debugf("Received: %s", command);
    
    // Simulate loading the movie data in the database.
    LOGGER.debugf("Created: %s", command.title());
    statusEmitter.send(MovieStatus.of(command.tmdbId(), MessageType.INGEST, MessageStatus.LOADED));
  }

}
