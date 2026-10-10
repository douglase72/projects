package com.erdouglass.emdb.media.movie.adapter.out.messaging;

import java.util.UUID;

import jakarta.data.Limit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.media.messaging.MediaEvent;
import com.erdouglass.emdb.media.messaging.MediaEvent.EventType;
import com.erdouglass.emdb.media.movie.adapter.out.persistence.MovieOutboxRepository;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.TmdbId;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.Scheduled.ConcurrentExecution;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

@ApplicationScoped
class MovieOutboxProducer {
  private static final Logger LOGGER = Logger.getLogger(MovieOutboxProducer.class);

  @Inject
  @Channel("media-events")
  MutinyEmitter<MediaEvent> emitter;
  
  @Inject
  MovieOutboxRepository events;
  
  @Scheduled(
      every = "{movie.outbox.interval}", 
      delayed = "{movie.outbox.delay}",
      concurrentExecution = ConcurrentExecution.SKIP)
  void publish() {
    for (var event : events.findAll(Limit.of(100))) {
      var message = MediaEvent.builder()
          .id(MessageId.of(event.getId()))
          .correlationId(CorrelationId.of(event.getCorrelationId()))
          .occurredAt(DateTimeFactory.now().toInstant())
          .tmdbId(TmdbId.of(event.getTmdbId()))
          .mediaType(MediaType.MOVIE)
          .eventType(EventType.SAVED)
          .build();
      var metadata = OutgoingKafkaRecordMetadata.<UUID>builder()
          .withKey(event.getCorrelationId())
          .build();    
      emitter.sendMessageAndAwait(Message.of(message).addMetadata(metadata));
      QuarkusTransaction.requiringNew().run(() -> events.delete(event)); 
      LOGGER.debugf("Published: %s", message);
    }
  }
}
