package com.erdouglass.emdb.ingest;

import java.util.NoSuchElementException;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.common.messaging.LoggingDecorator;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.SaveCommand;
import com.erdouglass.emdb.ingest.logging.Log;

import io.smallrye.reactive.messaging.rabbitmq.IncomingRabbitMQMetadata;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

public abstract class Scraper<T extends SaveCommand> {
  
  @Log
  public T scrape(Message<IngestMedia> message) {
    var metadata = message.getMetadata(IncomingRabbitMQMetadata.class)
        .orElseThrow(() -> new IllegalStateException("Missing RabbitMQ metadata"));
    var correlationId = metadata.getCorrelationId()
        .orElseThrow(() -> new NoSuchElementException("No correlation id.")); 
    var startTime = metadata.getHeaders().get(Configuration.START_TIME).toString();
    var command = getCommand(message.getPayload().tmdbId());
    getEmitter().send(Message.of(command)
        .addMetadata(OutgoingRabbitMQMetadata.builder()
            .withCorrelationId(correlationId)
            .withHeader(Configuration.START_TIME, startTime)
            .withHeader(LoggingDecorator.EVENT_TYPE, command.getClass().getSimpleName())
            .build()));    
    return command;
  }
  
  protected abstract T getCommand(int tmdbId);
  
  protected abstract Emitter<T> getEmitter();
}
