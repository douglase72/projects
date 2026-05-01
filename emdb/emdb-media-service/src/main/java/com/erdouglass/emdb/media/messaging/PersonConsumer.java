package com.erdouglass.emdb.media.messaging;

import java.time.Duration;
import java.time.Instant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.IngestMedia;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.event.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatus;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusContext;
import com.erdouglass.emdb.media.annotation.MessageMetadata;
import com.erdouglass.emdb.media.annotation.UpdateIngestStatus;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.service.PersonService;
import com.erdouglass.emdb.scraper.service.TmdbPersonScraper;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class PersonConsumer {
  private static final String ROUTE_KEY = "person.dlq";
  
  @Inject
  @Channel("person-dlq-out")
  Emitter<SavePerson> emitter;
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  TmdbPersonScraper scraper;
  
  @Inject
  PersonService service;
  
  @Inject 
  IngestStatusContext statusContext;
  
  @Inject
  CommandValidator validator;
  
  @UpdateIngestStatus
  public IngestStatusChanged ingest(Message<IngestMedia> message) {
    var correlationId = MessageMetadata.getCorrelationId(message);
    statusContext.setCorrelationId(correlationId);
    var tmdbId = message.getPayload().tmdbId();
    var command = service.findByTmdbId(tmdbId, null)
        .map(m -> scraper.extract(mapper.toSavePerson(m)))
        .orElseGet(() -> scraper.extract(SavePerson.builder().tmdbId(tmdbId).build()));
    
    try {
      validator.validate(command);
      var start = Instant.now();
      var person = service.save(command).entity();
      var et = Duration.between(start, Instant.now()).toMillis();
      return IngestStatusChanged.builder()          
          .id(correlationId)
          .tmdbId(person.getTmdbId())
          .status(IngestStatus.LOADED)
          .source(IngestSource.MEDIA)
          .type(MediaType.PERSON)
          .message(String.format("Ingest job for TMDB person %d persisted in %d ms", person.getTmdbId(), et))
          .emdbId(person.getId())
          .name(person.getName())
          .build();
    } catch (Exception e) {
      emitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(ROUTE_KEY)
          .withCorrelationId(correlationId.toString())
          .withHeader("X-Event-Type", command.getClass().getSimpleName())
          .build()));       
      throw e;
    }
  }
}
