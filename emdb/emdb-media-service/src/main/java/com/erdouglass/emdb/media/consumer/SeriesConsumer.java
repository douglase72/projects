package com.erdouglass.emdb.media.consumer;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.SeriesCreateCommand;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SeriesConsumer extends Consumer<SeriesCreateCommand> {
  private static final Logger LOGGER = Logger.getLogger(SeriesConsumer.class);
  
  @Blocking
  @Incoming("series")
  public CompletionStage<Void> consume(Message<SeriesCreateCommand> message) {
    return super.consume(message);
  }
  
  @Override
  protected void process(Message<SeriesCreateCommand> message) {
    var series = message.getPayload();
    LOGGER.infof("series: %s", series);
  }
  
}
