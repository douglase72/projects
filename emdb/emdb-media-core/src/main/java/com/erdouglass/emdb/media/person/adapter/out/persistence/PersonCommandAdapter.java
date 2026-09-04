package com.erdouglass.emdb.media.person.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.person.application.port.out.PersonCommandRepository;
import com.erdouglass.emdb.media.person.domain.model.Biography;
import com.erdouglass.emdb.media.person.domain.model.BirthDate;
import com.erdouglass.emdb.media.person.domain.model.DeathDate;
import com.erdouglass.emdb.media.person.domain.model.Person;
import com.erdouglass.emdb.media.person.domain.model.PersonDetails;
import com.erdouglass.emdb.media.person.domain.model.PersonId;

@ApplicationScoped
class PersonCommandAdapter implements PersonCommandRepository {
  
  @Inject
  JakartaDataPersonCommandRepository repository;

  @Override
  public Person insert(Person person) {
    return toPerson(repository.insert(toPersonEntity(person)));
  }
  
  @Override
  public List<Person> insertAll(List<Person> people) {
    return repository.insertAll(people.stream().map(this::toPersonEntity).toList())
        .stream()
        .map(this::toPerson)
        .toList();
  }

  @Override
  public Person update(Person person) {
    return toPerson(repository.update(toPersonEntity(person)));
  }

  @Override
  public Optional<Person> findByTmdbId(TmdbId tmdbId) {
    return repository.findByTmdbId(tmdbId.value()).map(this::toPerson);
  }
  
  @Override
  public List<Person> findByTmdbIdIn(List<TmdbId> tmdbIds) {
    return repository.findByTmdbIdIn(tmdbIds.stream().map(TmdbId::value).toList())
        .stream()
        .map(this::toPerson)
        .toList();
  }
  
  private PersonEntity toPersonEntity(Person person) {
    var entity = new PersonEntity();
    entity.setId(person.id().value());
    entity.setTmdbId(person.tmdbId().value());
    entity.setVersion(person.version().value());
    entity.setName(person.name().value());
    entity.setBirthDate(person.birthDate().map(BirthDate::toLocalDate).orElse(null));
    entity.setDeathDate(person.deathDate().map(DeathDate::toLocalDate).orElse(null));
    entity.setGender(person.gender().orElse(null));
    entity.setBiography(person.biography().map(Biography::value).orElse(null));
    return entity;
  }
  
  private Person toPerson(PersonEntity entity) {
    var id = PersonId.of(entity.getId());
    var tmdbId = TmdbId.of(entity.getTmdbId());
    var version = Version.of(entity.getVersion());
    var details = PersonDetails.builder()
        .name(Name.of(entity.getName()))
        .birthDate(entity.getBirthDate().map(BirthDate::from).orElse(null))
        .deathDate(entity.getDeathDate().map(DeathDate::from).orElse(null))
        .gender(entity.getGender().orElse(null))
        .biography(entity.getBiography().map(Biography::of).orElse(null))
        .build();
    return Person.rehydrate(id, tmdbId, version, details);
  }
}
