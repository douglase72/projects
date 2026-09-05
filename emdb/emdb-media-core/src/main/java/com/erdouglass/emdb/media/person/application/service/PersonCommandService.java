package com.erdouglass.emdb.media.person.application.service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.AggregateId;
import com.erdouglass.emdb.media.person.application.port.in.ResolvePersonCommand;
import com.erdouglass.emdb.media.person.application.port.in.ResolvePersonCommand.Reference;
import com.erdouglass.emdb.media.person.application.port.in.ResolvePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.in.Result;
import com.erdouglass.emdb.media.person.application.port.in.Result.Status;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonCommand;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.out.PersonCommandRepository;
import com.erdouglass.emdb.media.person.application.port.out.PersonEventPublisher;
import com.erdouglass.emdb.media.person.domain.model.Person;

@ApplicationScoped
class PersonCommandService implements SavePersonUseCase, ResolvePersonUseCase {
  private static final Logger LOGGER = Logger.getLogger(PersonCommandService.class);
  
  @Inject
  PersonEventPublisher events;
  
  @Inject
  PersonCommandRepository people;

  @Override
  @Transactional
  public Result save(SavePersonCommand command) {
    return people.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  @Override
  public Map<TmdbId, AggregateId> resolve(ResolvePersonCommand command) {
    var tmdbIds = command.references().stream().map(Reference::tmdbId).toList();
    var existing = people.findByTmdbIdIn(tmdbIds).stream()
        .collect(Collectors.toMap(Person::tmdbId, Function.identity()));    
    var stubs = command.references().stream()
        .filter(r -> !existing.containsKey(r.tmdbId()))
        .map(r -> Person.stub(r.tmdbId(), r.name()))
        .toList();
    for (var person : people.insertAll(stubs)) {
      existing.put(person.tmdbId(), person);
      LOGGER.debugf("Resolved: %s", person);
    } 
    events.publish(stubs.stream().flatMap(s -> s.pullEvents().stream()).toList());
    return existing.values().stream()
        .collect(Collectors.toMap(Person::tmdbId, Person::id));
  }
  
  private Result insert(SavePersonCommand command) {
    var person = Person.create(command.tmdbId(), command.details());
    var inserted = people.insert(person);
    LOGGER.debugf("Saved: %s", inserted);
    return Result.of(inserted.id(), inserted.version(), Status.CREATED);
  }
  
  private Result update(Person existing, SavePersonCommand command) {
    existing.update(command.details());
    var updated = people.update(existing);
    LOGGER.debugf("Saved: %s", updated);
    return Result.of(updated.id(), updated.version(), Status.UPDATED);
  }
}