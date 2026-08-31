package com.erdouglass.emdb.media.person.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.api.IngestCommand;
import com.erdouglass.emdb.ingest.api.IngestFacade;
import com.erdouglass.emdb.ingest.api.IngestType;

@ApplicationScoped
class PersonProducer {
  
  @Inject
  IngestFacade facade;
  
  void onPersonsRegistered(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) PersonsRegisteredEvent event) {
    event.tmdbIds().forEach(id -> facade.ingest(IngestCommand.of(id, IngestType.PERSON)));
  }
}
