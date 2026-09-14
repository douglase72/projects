package com.erdouglass.emdb.media.person.application.service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SavePersonCommand;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.person.application.port.in.ResolvePersonCommand;
import com.erdouglass.emdb.media.person.application.port.in.ResolvePersonCommand.Reference;
import com.erdouglass.emdb.media.person.application.port.in.ResolvePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.out.PersonCommandRepository;
import com.erdouglass.emdb.media.person.application.port.out.PersonOutboxRepository;
import com.erdouglass.emdb.media.person.domain.model.Person;
import com.erdouglass.emdb.media.person.domain.model.PersonDetails;

@ApplicationScoped
class PersonCommandService implements SavePersonUseCase, ResolvePersonUseCase {
  private static final Logger LOGGER = Logger.getLogger(PersonCommandService.class);
  
  @Inject
  PersonCommandRepository people;
  
  @Inject
  PersonOutboxRepository outbox;
  
  @Override
  @Transactional
  public void save(SavePersonCommand command) {
    var details = PersonDetailsMapper.toPersonDetails(command);
    var person = Person.create(TmdbId.of(command.tmdbId()), details);
    people.insert(person);
    LOGGER.infof("Created: %s", person);
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
    return existing.values().stream()
        .collect(Collectors.toMap(Person::tmdbId, Person::id));
  }
}
