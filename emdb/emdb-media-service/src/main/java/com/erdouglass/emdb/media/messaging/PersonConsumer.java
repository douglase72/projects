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
import com.erdouglass.emdb.media.annotation.IngestContext;
import com.erdouglass.emdb.media.annotation.UpdateStatus;
import com.erdouglass.emdb.media.api.command.SavePerson;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.service.CommandValidator;
import com.erdouglass.emdb.media.service.PersonCrudService;
import com.erdouglass.emdb.media.service.TmdbImageService;
import com.erdouglass.emdb.media.service.TmdbPersonScraper;
import com.erdouglass.emdb.media.utils.MessageMetadata;
import com.erdouglass.emdb.messaging.api.IngestSource;
import com.erdouglass.emdb.messaging.api.IngestStatus;
import com.erdouglass.emdb.messaging.api.IngestStatusChanged;
import com.google.common.base.Objects;

import io.micrometer.core.instrument.MeterRegistry;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class PersonConsumer {
  private static final String ROUTE_KEY = "person.dlq";
  
  @Inject
  IngestContext context;
  
  @Inject
  @Channel("person-dlq-out")
  Emitter<SavePerson> emitter;  
  
  @Inject
  TmdbImageService imageService;
  
  @Inject
  MeterRegistry registry;
  
  @Inject
  TmdbPersonScraper scraper;
  
  @Inject
  PersonCrudService service;
  
  @Inject
  CommandValidator validator;
  
  @UpdateStatus
  public IngestStatusChanged ingest(Message<IngestMedia> message) {
    context.setCorrelationId(MessageMetadata.getCorrelationId(message));
    var tmdbId = message.getPayload().tmdbId();
    var existingPerson = service.findByTmdbId(tmdbId, null);
    var command = service.findByTmdbId(tmdbId, null)
        .map(p -> scraper.extract(p))
        .orElseGet(() -> scraper.extract(defaultPerson(tmdbId)));
        
    try {
      var start = Instant.now();
      validator.validate(command);
      var person = service.save(command).entity();
      existingPerson.ifPresent(p -> deleteImages(p, person));
      var et = Duration.between(start, Instant.now());
      context.setPersistDuration(et);
      return IngestStatusChanged.builder()
          .id(context.getCorrelationId())
          .tmdbId(person.getTmdbId())
          .status(IngestStatus.LOADED)
          .source(IngestSource.MEDIA)
          .type(MediaType.PERSON)
          .message(String.format("Ingest job for TMDB person %d persisted in %d ms", person.getTmdbId(), et.toMillis()))
          .emdbId(person.getId())
          .name(person.getName())
          .build();
    } catch (ConstraintViolationException e) {
      emitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(ROUTE_KEY)
          .withCorrelationId(context.getCorrelationId().toString())
          .withHeader("X-Event-Type", command.getClass().getSimpleName())
          .build())); 
      throw e;     
    }
  }
  
  private Person defaultPerson(int tmdbId) {
    var person = new Person();
    person.setTmdbId(tmdbId);
    return person;
  }
  
  private void deleteImages(Person oldPerson, Person newPerson) {
    if (!Objects.equal(oldPerson.getTmdbProfile(), newPerson.getTmdbProfile())) {
      if (oldPerson.getProfile() != null) {
        imageService.delete(oldPerson.getProfile());
      }
    }
  }
}
