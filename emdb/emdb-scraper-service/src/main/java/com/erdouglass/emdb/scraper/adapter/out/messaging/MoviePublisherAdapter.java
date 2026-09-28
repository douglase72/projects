package com.erdouglass.emdb.scraper.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.messaging.MovieScraped;
import com.erdouglass.emdb.scraper.application.port.out.MoviePublisher;
import com.erdouglass.emdb.scraper.domain.model.Movie;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

@ApplicationScoped
class MoviePublisherAdapter implements MoviePublisher {
  private static final Logger LOGGER = Logger.getLogger(MoviePublisherAdapter.class);
  
  @Inject
  @Channel("movies-scraped")
  MutinyEmitter<MovieScraped> emitter;
  
  @Inject
  MovieMapper mapper;

  @Override
  public void publish(Movie movie) {
    var event = mapper.toMovieScraped(movie);
    var metadata = OutgoingKafkaRecordMetadata.<Integer>builder()
        .withKey(event.tmdbId().value())
        .build();
    emitter.sendMessageAndAwait(Message.of(event).addMetadata(metadata));
    LOGGER.infof("Published: %s", event);
  }
}
