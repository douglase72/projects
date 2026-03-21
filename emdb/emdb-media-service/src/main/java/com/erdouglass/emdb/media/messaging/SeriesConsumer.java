package com.erdouglass.emdb.media.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.MDC;

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.scraper.service.TmdbSeriesScraper;
import com.erdouglass.webservices.LoggingDecorator;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class SeriesConsumer extends Consumer {
  private static final String ROUTE_KEY = "series.invalid";
  
  @Inject
  @Channel("series-dlq-out")
  Emitter<SaveSeries> dlqEmitter;
  
  @Inject
  TmdbSeriesScraper scraper;

  @Override
  public void ingest(@NotNull @Positive Integer tmdbId) {
    var command = scraper.extract(tmdbId);
    
    try {
      validate(command);
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
