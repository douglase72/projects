package com.erdouglass.emdb.media.consumer;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieMessage;
import com.erdouglass.emdb.media.mapper.MovieMapper;
import com.erdouglass.emdb.media.service.MovieService;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MovieConsumer extends Consumer<MovieMessage> {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @Inject
  MovieMapper movieMapper;
  
  @Inject
  MovieService movieService;

  @Blocking
  @Incoming("movies")
  public CompletionStage<Void> consume(Message<MovieMessage> message) {
    return super.consume(message);
  }
  
  @Override
  protected void process(Message<MovieMessage> message) {
    var movie = message.getPayload().movie();
    movieService.create(movieMapper.toMovie(movie));

    var people = message.getPayload().people();
    LOGGER.infof("people: %d", people.size());
    
    var credits = message.getPayload().credits();
    LOGGER.infof("credits: %d", credits.size());    
  }

}
