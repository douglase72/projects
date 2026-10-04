package com.erdouglass.emdb.media.movie.adapter.in.messaging;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.Correlation;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.scraper.messaging.MovieScrapedMessage;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
class MovieConsumer {
  
  @Inject
  SaveMovieUseCase saveUseCase;

  @Blocking
  @Incoming("movies-scraped")
  CompletionStage<Void> onMessage(Message<MovieScrapedMessage> message) {
    var command = MovieMapper.toMovieScrapedMessage(message.getPayload());
    saveUseCase.save(command, Correlation.from(message));
    return message.ack();
  }
}
