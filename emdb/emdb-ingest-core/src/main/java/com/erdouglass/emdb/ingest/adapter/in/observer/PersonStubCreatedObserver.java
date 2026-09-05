package com.erdouglass.emdb.ingest.adapter.in.observer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.in.IngestMediaCommand;
import com.erdouglass.emdb.ingest.application.port.in.SubmitIngestUseCase;
import com.erdouglass.emdb.ingest.domain.model.IngestType;
import com.erdouglass.emdb.media.api.PersonStubCreated;

@ApplicationScoped
class PersonStubCreatedObserver {
  
  @Inject
  SubmitIngestUseCase submitUseCase;

  void onPersonStubCreated(@Observes(during = TransactionPhase.AFTER_SUCCESS) PersonStubCreated event) {
    submitUseCase.submit(IngestMediaCommand.of(event.tmdbId(), IngestType.PERSON));
  }
}
