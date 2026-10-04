package com.erdouglass.emdb.scraper.adapter.out.messaging;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.Correlation;
import com.erdouglass.emdb.scraper.application.port.out.MoviePublisher;
import com.erdouglass.emdb.scraper.domain.model.Movie;
import com.erdouglass.emdb.scraper.messaging.MovieScrapedMessage;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

@ApplicationScoped
class MovieAdapter implements MoviePublisher {
  
  @Inject
  @Channel("movies-scraped")
  MutinyEmitter<MovieScrapedMessage> emitter;
  
  @Inject
  MovieMapper mapper;

  @Override
  public void publish(UUID ingestId, Movie movie) {
    var event = mapper.toMovieScraped(movie);
    var metadata = OutgoingKafkaRecordMetadata.<Integer>builder()
        .withKey(event.tmdbId().value())
        .withHeaders(Correlation.of("ingest-id", ingestId.toString()).toHeaders())
        .build();
    emitter.sendMessageAndAwait(Message.of(event).addMetadata(metadata));
  }
}
