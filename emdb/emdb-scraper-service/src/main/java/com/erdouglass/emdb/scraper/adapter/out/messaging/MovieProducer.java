package com.erdouglass.emdb.scraper.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.scraper.application.port.out.MoviePublisher;
import com.erdouglass.emdb.scraper.messaging.MovieExtracted;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

/// Publishes movie extracted events to the broker.
/// 
/// Publish the [MovieExtracted] event to the emdb.extract.movies Kafka topic 
/// keyed by the movie TMDB id. 
@ApplicationScoped
class MovieProducer implements MoviePublisher {
  
  @Inject
  @Channel("extract-movies")
  MutinyEmitter<MovieExtracted> emitter;

  @Override
  public void publish(MovieExtracted event) {
    var metadata = OutgoingKafkaRecordMetadata.<Integer>builder()
        .withKey(event.tmdbId().value())
        .build();
    emitter.sendMessageAndAwait(Message.of(event).addMetadata(metadata));
  }
}
