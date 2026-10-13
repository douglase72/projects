package com.erdouglass.emdb.scraper.adapter.in.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.erdouglass.emdb.ingest.messaging.IngestCommand;
import com.erdouglass.emdb.scraper.application.port.in.ScrapeMediaCommand;
import com.erdouglass.emdb.scraper.application.port.in.ScrapeMovieUseCase;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.CorrelationScope;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
class IngestCommandConsumer {
  
  @Inject
  ScrapeMovieUseCase movieUseCase;
  
  @Blocking
  @Incoming("ingest-commands")
  void onMessage(IngestCommand command) {
    try (var _ = CorrelationScope.open(CorrelationId.of(command.correlationId().value()))) {
      var cmd = ScrapeMediaCommand.builder()
          .id(command.id())
          .correlationId(command.correlationId())
          .submittedAt(command.submittedAt())
          .tmdbId(command.tmdbId())
          .build();
      switch (command.mediaType()) {
        case MOVIE  -> movieUseCase.scrape(cmd);
        case PERSON -> throw new UnsupportedOperationException();
        case SERIES -> throw new UnsupportedOperationException();
      }
    }
  }
}
