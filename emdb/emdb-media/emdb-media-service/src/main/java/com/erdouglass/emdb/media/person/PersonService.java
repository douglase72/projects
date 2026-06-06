package com.erdouglass.emdb.media.person;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.PersonCredit;
import com.erdouglass.emdb.media.command.SavePerson;
import com.erdouglass.emdb.media.image.ImageService;
import com.erdouglass.emdb.media.internal.PersonResolver;
import com.erdouglass.emdb.media.logging.Log;

/// Application service that persists [Person] aggregates and their profile
/// images, and resolves the people referenced by a batch of credits. Reconciles
/// each [SavePerson] command against existing records, inserting a new person or
/// merging an update, and cleaning up any image it replaces.
@ApplicationScoped
class PersonService implements PersonResolver {
  
  @Inject
  ImageService imageService;
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonRepository repository;
  
  /// Persists a person from the command, creating them when no person with the
  /// same TMDB id exists and updating the existing one otherwise. The profile
  /// image is saved or replaced as needed.
  ///
  /// @param command the person data to persist
  /// @return the saved person, with generated identifiers populated
  @Log
  @Transactional
  public Person save(final SavePerson command) {
    Person person;
    var existing = repository.findByTmdbId(command.tmdbId()).orElse(null); 
    if (existing == null) {
      var profile = imageService.save(command.profile());
      person = repository.insert(mapper.toPerson(command, profile));
    } else {
      var profile = imageService.update(existing.getTmdbProfile(), existing.getProfile(), command.profile());
      var cmd = SavePerson.builder(command)
          .profile(profile.image())
          .build();
      mapper.merge(cmd, existing);
      person = repository.update(existing);
      profile.toDelete().ifPresent(imageService::delete);
    }    
    return person;
  }

  @Override
  public Map<Integer, Person> findOrCreate(List<PersonCredit> credits) {
    if (credits.isEmpty()) {
      return Map.of();
    }
    var distinct = credits.stream()
        .collect(Collectors.toMap(PersonCredit::tmdbId, Function.identity(), (a, _) -> a));
    var existing = repository.findByTmdbIdIn(List.copyOf(distinct.keySet())).stream()
        .collect(Collectors.toMap(Person::getTmdbId, Function.identity())); 
    var peopleToInsert = distinct.values().stream()
        .filter(c -> !existing.containsKey(c.tmdbId()))
        .map(PersonService::toPerson)
        .toList();
    for (var person : repository.insertAll(peopleToInsert)) {
      var tmdbId = person.getTmdbId();
      existing.put(tmdbId, person);
    }
    return existing;    
  }
  
  /// Builds a new transient [Person] from a credit, copying the TMDB id, name,
  /// and gender. Used to create people that do not yet exist during batch
  /// resolution.
  ///
  /// @param credit the credit to derive a person from
  /// @return a new, unpersisted person
  private static Person toPerson(PersonCredit credit) {
    var person = new Person(credit.tmdbId());
    person.setName(credit.name());
    person.setGender(credit.gender());
    return person;
  }  
}
