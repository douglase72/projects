package com.erdouglass.emdb.media.person.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.dto.SaveResult.Status;
import com.erdouglass.emdb.media.dto.UpdateResult;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonCommand;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.in.UpdatePersonCommand;
import com.erdouglass.emdb.media.person.application.port.in.UpdatePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.out.PersonCommandRepository;
import com.erdouglass.emdb.media.person.domain.event.PersonEvent;
import com.erdouglass.emdb.media.person.domain.exception.PersonNotFoundException;
import com.erdouglass.emdb.media.person.domain.model.Person;

@ApplicationScoped
class PersonCommandService implements SavePersonUseCase, UpdatePersonUseCase {
  
  @Inject
  Event<PersonEvent> emitter;
  
  @Inject
  PersonCommandRepository people;

  /// Save the person described by the command to the database.
  /// 
  /// This method is idempotent with respect to the persons TMDB id. If a person
  /// with a matching TMDB id does not already exist, one will be created. 
  /// Otherwise, the persons details are updated making retries safe.
  @Override
  @Transactional
  public SaveResult save(SavePersonCommand command) {
    return people.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  @Override
  @Transactional
  public UpdateResult update(UpdatePersonCommand command) {
    var existing = people.findById(command.id())
        .orElseThrow(() -> new PersonNotFoundException(command.id().value().toString()));
    existing.checkVersion(command.version());
    existing.update(command.details());
    var updated = people.update(existing);
    existing.pullEvents().forEach(emitter::fire);
    return UpdateResult.of(updated.id(), updated.version(), UpdateResult.Status.UPDATED);
  }
  
  private SaveResult insert(SavePersonCommand command) {
    var person = Person.create(command.tmdbId(), command.details());
    var inserted = people.insert(person);
    person.pullEvents().forEach(emitter::fire);
    return SaveResult.of(inserted.id(), Status.CREATED);
  }
  
  private SaveResult update(Person existing, SavePersonCommand command) {
    existing.update(command.details());
    var updated = people.update(existing);
    existing.pullEvents().forEach(emitter::fire);
    return SaveResult.of(updated.id(), Status.UPDATED);
  }
}
