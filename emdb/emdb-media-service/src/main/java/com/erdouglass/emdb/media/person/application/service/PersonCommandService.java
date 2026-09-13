package com.erdouglass.emdb.media.person.application.service;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SavePersonCommand;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonUseCase;
import com.erdouglass.emdb.media.person.domain.model.Person;

@ApplicationScoped
class PersonCommandService implements SavePersonUseCase {
  private static final Logger LOGGER = Logger.getLogger(PersonCommandService.class);
  
  @Override
  public void save(SavePersonCommand command) {
    var details = PersonDetailsMapper.toPersonDetails(command);
    var person = Person.create(TmdbId.of(command.tmdbId()), details);
    LOGGER.infof("person: %s", person);
  }
}
