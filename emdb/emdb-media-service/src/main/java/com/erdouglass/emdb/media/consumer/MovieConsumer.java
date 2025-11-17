package com.erdouglass.emdb.media.consumer;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.common.command.MovieCreateCommand;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;

import io.smallrye.reactive.messaging.annotations.Blocking;

@ApplicationScoped
public class MovieConsumer extends Consumer<MovieCreateCommand> {
  
  @Inject
  MovieMapper movieMapper;
  
  @Inject
  MovieService movieService;

  @Blocking
  @Incoming("movies")
  public CompletionStage<Void> consume(Message<MovieCreateCommand> message) {
    return super.consume(message);
  }
  
  @Override
  protected void process(Message<MovieCreateCommand> message) {
    movieService.create(movieMapper.toMovie(message.getPayload()));
  }

}
