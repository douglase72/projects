package com.erdouglass.emdb.media.consumer;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieMessage;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovieConsumer extends Consumer<MovieMessage> {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);

  @Blocking
  @Incoming("movies")
  public CompletionStage<Void> consume(Message<MovieMessage> message) {
    return super.consume(message);
  }
  
  @Override
  protected void process(Message<MovieMessage> message) {
    var movie = message.getPayload().movie();
    LOGGER.infof("movie: %s", movie);

    var people = message.getPayload().people();
    LOGGER.infof("people: %d", people.size());
    
    var credits = message.getPayload().credits();
    LOGGER.infof("credits: %d", credits.size());    
  }

}
