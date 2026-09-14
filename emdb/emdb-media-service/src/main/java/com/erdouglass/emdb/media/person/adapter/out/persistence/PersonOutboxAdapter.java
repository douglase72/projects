package com.erdouglass.emdb.media.person.adapter.out.persistence;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.person.application.port.out.PersonOutboxRepository;
import com.erdouglass.emdb.media.person.domain.event.DomainEvent;

@ApplicationScoped
class PersonOutboxAdapter implements PersonOutboxRepository {
  
  @Inject
  JakartaDataPersonOutboxRepository repository;

  @Override
  public void saveAll(List<DomainEvent> events) {
    repository.insertAll(events.stream().map(this::toPersonOutboxEntity).toList());
  }
  
  private PersonOutboxEntity toPersonOutboxEntity(DomainEvent event) {
    var entity = new PersonOutboxEntity();
    entity.setId(event.id().value());
    entity.setTmdbId(event.tmdbId().value());
    entity.setName(event.name().value());
    entity.setCreatedAt(event.createdAt().toInstant());
    return entity;
  }
}
