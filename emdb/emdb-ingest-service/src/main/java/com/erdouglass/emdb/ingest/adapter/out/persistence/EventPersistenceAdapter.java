package com.erdouglass.emdb.ingest.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.out.EventRepository;
import com.erdouglass.emdb.ingest.domain.event.DomainEvent;

@ApplicationScoped
class EventPersistenceAdapter implements EventRepository {
  
  @Inject
  JakartaDataEventRepository events;

  @Override
  public boolean insertIfAbsent(DomainEvent event) {
    return events.insertIfAbsent(event.messageId().value(), event.occurredAt());
  }
}
