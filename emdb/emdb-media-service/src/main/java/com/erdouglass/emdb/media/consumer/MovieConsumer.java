package com.erdouglass.emdb.media.consumer;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.command.MovieCreateCommand;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
public class MovieConsumer {
  private static final Logger LOGGER = Logger.getLogger(MovieConsumer.class);
  
  @RunOnVirtualThread
  @Incoming("movie-create-in")
  public void onMessage(MovieCreateCommand message) {
    LOGGER.infof("Received: %s", message);
  }

}
