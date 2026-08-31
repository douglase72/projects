package com.erdouglass.emdb.media.person.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.kernel.Result;
import com.erdouglass.emdb.media.kernel.Result.Status;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonCommand;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.out.PersonCommandRepository;
import com.erdouglass.emdb.media.person.domain.model.Person;
import com.erdouglass.emdb.media.person.domain.model.PersonPublicId;

@ApplicationScoped
class PersonCommandService implements SavePersonUseCase {
  private static final Logger LOGGER = Logger.getLogger(PersonCommandService.class);
  
  @Inject
  PersonCommandRepository people;

  /// Save the person described by the command.
  /// 
  /// Create the person if they do not already exist, otherwise update the 
  /// existing person.
  /// 
  /// @param command the command that describes the [Person]
  /// @return the result
  @Override
  @Transactional
  public Result save(SavePersonCommand command) {
    return people.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  private Result insert(SavePersonCommand command) {
    var person = Person.create(command.tmdbId(), command.details());
    var inserted = people.insert(person);
    LOGGER.infof("Saved: %s", inserted);
    return Result.of(
        inserted.publicId().map(PersonPublicId::value).orElseThrow(),
        inserted.version().value(), 
        Status.CREATED);
  }
  
  private Result update(Person existing, SavePersonCommand command) {
    existing.update(command.details());
    people.update(existing);
    LOGGER.infof("Saved: %s", existing);
    return Result.of(
        existing.publicId().map(PersonPublicId::value).orElseThrow(),
        existing.version().value(), 
        Status.UPDATED);
  }
}
