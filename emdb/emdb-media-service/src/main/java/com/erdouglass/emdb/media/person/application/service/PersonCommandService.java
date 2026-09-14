package com.erdouglass.emdb.media.person.application.service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SavePersonCommand;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.SaveResult;
import com.erdouglass.emdb.media.kernel.SaveResult.Status;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.person.application.port.in.ResolvePersonCommand;
import com.erdouglass.emdb.media.person.application.port.in.ResolvePersonCommand.Reference;
import com.erdouglass.emdb.media.person.application.port.in.ResolvePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.out.PersonCommandRepository;
import com.erdouglass.emdb.media.person.application.port.out.PersonOutboxRepository;
import com.erdouglass.emdb.media.person.domain.event.DomainEvent;
import com.erdouglass.emdb.media.person.domain.model.Person;
import com.erdouglass.emdb.media.person.domain.model.PersonDetails;

@ApplicationScoped
class PersonCommandService implements SavePersonUseCase, ResolvePersonUseCase {
  private static final Logger LOGGER = Logger.getLogger(PersonCommandService.class);
  
  @Inject
  Event<DomainEvent> emitter;
  
  @Inject
  PersonCommandRepository people;
  
  @Inject
  PersonOutboxRepository outbox;
  
  /// Save the person described by the command to the database.
  /// 
  /// This method is idempotent with respect to the persons TMDB id. If a person
  /// with a matching TMDB id does not already exist, one will be created. 
  /// Otherwise, the persons details are updated making retries safe.
  @Override
  @Transactional
  public SaveResult save(SavePersonCommand command) {
    return people.findByTmdbId(TmdbId.of(command.tmdbId()))
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }

  @Override
  @Transactional
  public Map<TmdbId, PublicId> resolve(ResolvePersonCommand command) {
    var tmdbIds = command.references().stream().map(Reference::tmdbId).toList();
    var existing = people.findByTmdbIdIn(tmdbIds).stream()
        .collect(Collectors.toMap(Person::tmdbId, Function.identity())); 
    var stubs = command.references().stream()
        .map(r -> Person.create(r.tmdbId(), PersonDetails.builder().name(r.name()).build()))
        .toList();
    for (var person : people.insertAll(stubs)) {
      existing.put(person.tmdbId(), person);
    }
    var events = stubs.stream().flatMap(s -> s.pullEvents().stream()).toList();
    outbox.saveAll(events);
    events.stream().findAny().ifPresent(emitter::fire);
    return existing.values().stream()
        .collect(Collectors.toMap(Person::tmdbId, Person::id));
  }
  
  private SaveResult insert(SavePersonCommand command) {
    var person = Person.create(TmdbId.of(command.tmdbId()), PersonDetailsMapper.toPersonDetails(command));
    var inserted = people.insert(person);
    LOGGER.infof("Created: %s", person);
    return SaveResult.of(inserted.id(), Status.CREATED);
  }
  
  private SaveResult update(Person existing, SavePersonCommand command) {
    existing.update(PersonDetailsMapper.toPersonDetails(command));
    var updated = people.update(existing);
    LOGGER.infof("Updated: %s", updated);
    return SaveResult.of(updated.id(), Status.UPDATED);
  }
}
