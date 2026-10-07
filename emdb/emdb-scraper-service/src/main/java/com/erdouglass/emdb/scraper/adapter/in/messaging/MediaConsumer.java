package com.erdouglass.emdb.scraper.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.ingest.messaging.IngestCommand;
import com.erdouglass.emdb.scraper.application.port.in.ScrapeMediaCommand;
import com.erdouglass.emdb.scraper.application.port.in.ScrapeMovieUseCase;

import io.smallrye.common.annotation.Blocking;

/// Consumes ingest commands from the broker.
/// 
/// Consume the [IngestCommand] from the RabbitMQ broker one at a time. There 
/// can only be one active consumer at a time to ensure commands are never 
/// processed concurrently. 
@ApplicationScoped
class MediaConsumer {
  
  @Inject
  ScrapeMovieUseCase movieUseCase;
  
  @Blocking
  @Incoming("ingest-media")
  void onMessage(IngestCommand command) {
    var cmd = ScrapeMediaCommand.of(
        command.messageId(), 
        command.correlationId(), 
        command.tmdbId(), 
        command.submittedAt());
    switch (command.mediaType()) {
      case MOVIE  -> movieUseCase.scrape(cmd);
      case PERSON -> throw new UnsupportedOperationException();
      case SERIES -> throw new UnsupportedOperationException();
    }    
  }
}
