package com.erdouglass.emdb.media.messaging;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.common.api.MediaType;
import com.erdouglass.emdb.common.api.command.IngestMedia;
import com.erdouglass.emdb.common.api.messaging.IngestSource;
import com.erdouglass.emdb.common.api.messaging.IngestStatus;
import com.erdouglass.emdb.common.api.messaging.IngestStatusChanged;
import com.erdouglass.emdb.media.annotation.CorrelationContext;
import com.erdouglass.emdb.media.annotation.UpdateStatus;
import com.erdouglass.emdb.media.api.command.SavePerson;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.service.CommandValidator;
import com.erdouglass.emdb.media.service.PersonCrudService;
import com.erdouglass.emdb.media.service.TmdbPersonScraper;
import com.erdouglass.emdb.media.utils.MessageMetadata;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class PersonConsumer {
  private static final String ROUTE_KEY = "person.dlq";
  
  @Inject
  CorrelationContext correlation;
  
  @Inject
  @Channel("person-dlq-out")
  Emitter<SavePerson> emitter;  
  
  @Inject
  TmdbPersonScraper scraper;
  
  @Inject
  PersonCrudService service;
  
  @Inject
  CommandValidator validator;
  
  @UpdateStatus
  public IngestStatusChanged ingest(Message<IngestMedia> message) {
    correlation.setId(MessageMetadata.getCorrelationId(message));
    var tmdbId = message.getPayload().tmdbId();
    var command = service.findByTmdbId(tmdbId, null)
        .map(p -> scraper.extract(p))
        .orElseGet(() -> {
          var person = new Person();
          person.setTmdbId(tmdbId);
          return scraper.extract(person);
        });
        
    try {
      var start = Instant.now();
      validator.validate(command);
      var person = service.save(command).entity();
      var et = Duration.between(start, Instant.now()).toMillis();
      return IngestStatusChanged.builder()
          .id(correlation.getId())
          .tmdbId(person.getTmdbId())
          .status(IngestStatus.LOADED)
          .source(IngestSource.MEDIA)
          .type(MediaType.PERSON)
          .message(String.format("Ingest job for TMDB person %d persisted in %d ms", person.getTmdbId(), et))
          .emdbId(person.getId())
          .name(person.getName())
          .build();
    } catch (ConstraintViolationException e) {
      emitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(ROUTE_KEY)
          .withCorrelationId(correlation.getId().toString())
          .withHeader("X-Event-Type", command.getClass().getSimpleName())
          .build())); 
      throw e;     
    }
  }
}
