package com.erdouglass.emdb.media.consumer;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.SeriesMessage;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SeriesConsumer extends Consumer<SeriesMessage> {
  private static final Logger LOGGER = Logger.getLogger(SeriesConsumer.class);
  
  @Blocking
  @Incoming("series")
  public CompletionStage<Void> consume(Message<SeriesMessage> message) {
    return super.consume(message);
  }
  
  @Override
  protected void process(Message<SeriesMessage> message) {
    var series = message.getPayload().series();
    LOGGER.infof("series: %s", series);
    
    var people = message.getPayload().people();
    LOGGER.infof("people: %d", people.size());
    
    var credits = message.getPayload().credits();
    LOGGER.infof("credits: %d", credits.size());    
  }
  
}
