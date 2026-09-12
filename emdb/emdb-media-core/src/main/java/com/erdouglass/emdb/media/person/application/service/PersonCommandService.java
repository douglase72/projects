package com.erdouglass.emdb.media.person.application.service;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.PublicId;
import com.erdouglass.emdb.media.SavePersonCommand;
import com.erdouglass.emdb.media.SavePersonUseCase;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveResult.Status;

@ApplicationScoped
class PersonCommandService implements SavePersonUseCase {
  private static final Logger LOGGER = Logger.getLogger(PersonCommandService.class);
  
  @Override
  public SaveResult save(SavePersonCommand command) {
    LOGGER.infof("command: %s", command);
    return SaveResult.of(PublicId.newId(), Status.CREATED);
  }
}
