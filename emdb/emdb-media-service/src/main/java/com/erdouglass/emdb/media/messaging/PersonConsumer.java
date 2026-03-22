package com.erdouglass.emdb.media.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.service.PersonService;
import com.erdouglass.emdb.scraper.service.TmdbPersonScraper;
import com.erdouglass.webservices.LoggingDecorator;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class PersonConsumer extends Consumer {
  private static final String ROUTE_KEY = "person.invalid";
  
  @Inject
  @Channel("person-dlq-out")
  Emitter<SavePerson> dlqEmitter;
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  TmdbPersonScraper scraper;
  
  @Inject
  PersonService service;

  @Override
  public void ingest(@NotNull @Positive Integer tmdbId) {
    var command = service.findByTmdbId(tmdbId)
        .map(p -> scraper.extract(mapper.toSavePerson(p)))
        .orElseGet(() -> scraper.extract(tmdbId));
    
    try {
      validate(command);
      service.save(command);
    } catch (Exception e) {
      dlqEmitter.send(Message.of(command)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(ROUTE_KEY)
          .withCorrelationId((String) MDC.get(LoggingDecorator.CORRELATION_ID))
          .withHeader("X-Event-Type", command.getClass().getSimpleName())
          .build())); 
      throw new RuntimeException(e);
    }    
  }

}
