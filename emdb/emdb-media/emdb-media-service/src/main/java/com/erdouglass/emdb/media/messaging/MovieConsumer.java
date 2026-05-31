package com.erdouglass.emdb.media.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.media.domain.MovieService;
import com.erdouglass.emdb.media.movie.SaveMovie;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

/// Consumes [SaveMovie] commands from the RabbitMQ save-movie queue.
@ApplicationScoped
public class MovieConsumer extends Consumer<SaveMovie> {
  
  @Inject
  MovieService movieService;

  @Override 
  @RunOnVirtualThread
  @Incoming("save-movie-in")
  public Uni<Void> onMessage(Message<SaveMovie> message) { 
    return consume(message);
  }

  @Override
  protected void save(final SaveMovie command) {
    movieService.save(command);
  }
}
