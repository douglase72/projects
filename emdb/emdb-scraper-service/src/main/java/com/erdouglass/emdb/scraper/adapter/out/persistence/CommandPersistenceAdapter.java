package com.erdouglass.emdb.scraper.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.scraper.application.port.out.CommandRepository;

@ApplicationScoped
class CommandPersistenceAdapter implements CommandRepository {
  
  @Inject
  JakartaDataCommandRepository commands;

  @Override
  @Transactional
  public boolean hasProcessed(MessageId id) {
    return commands.findById(id.value()).isPresent();
  }

  @Override
  @Transactional
  public void markProcessed(MessageId id) {
    var entity = new CommandEntity();
    entity.setId(id.value());
    commands.insert(entity);
  }
}
