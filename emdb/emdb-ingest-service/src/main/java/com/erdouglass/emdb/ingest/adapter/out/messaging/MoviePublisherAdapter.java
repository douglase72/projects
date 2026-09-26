package com.erdouglass.emdb.ingest.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.ingest.application.port.out.MoviePublisher;
import com.erdouglass.emdb.media.messaging.SaveMovieMessage;

@ApplicationScoped
class MoviePublisherAdapter implements MoviePublisher {
  private static final Logger LOGGER = Logger.getLogger(MoviePublisherAdapter.class);
  
  @Inject
  @Channel("save-movie")
  Emitter<SaveMovieMessage> emitter;

  /// Publish the movie to the RabbitMQ exchange.
  @Override
  public void publish(Movie movie) {
    var message = SaveMovieMessage.builder()
        .tmdbId(movie.tmdbId())
        .title(movie.title())
        .releaseDate(movie.releaseDate())
        .score(movie.score())
        .originalLanguage(movie.originalLanguage())
        .overview(movie.overview())
        .build(); 
    try {
      emitter.send(message);
      LOGGER.infof("Published: %s", message);
    } catch (Exception e) {
      LOGGER.errorf(e, "Failed to publish: %s", message);
    }
  }
}
