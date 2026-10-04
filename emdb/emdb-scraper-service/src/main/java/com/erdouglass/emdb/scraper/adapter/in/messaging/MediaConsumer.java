package com.erdouglass.emdb.scraper.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.ingest.messaging.IngestMediaMessage;
import com.erdouglass.emdb.scraper.application.port.in.ScrapeMediaCommand;
import com.erdouglass.emdb.scraper.application.port.in.ScrapeMovieUseCase;

import io.smallrye.common.annotation.RunOnVirtualThread;

@ApplicationScoped
class MediaConsumer {
  
  @Inject
  ScrapeMovieUseCase movieUseCase;

  @RunOnVirtualThread
  @Incoming("ingest-media")
  void onMessage(IngestMediaMessage message) {
    var command = ScrapeMediaCommand.of(message.ingestId(), message.tmdbId(), message.submittedAt());
    switch (message.mediaType()) {
      case MOVIE  -> movieUseCase.scrape(command);
      case PERSON -> throw new UnsupportedOperationException();
      case SERIES -> throw new UnsupportedOperationException();
    }
  }
}
