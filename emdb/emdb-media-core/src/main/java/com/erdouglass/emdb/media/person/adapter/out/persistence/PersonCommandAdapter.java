package com.erdouglass.emdb.media.person.adapter.out.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.person.application.port.out.PersonCommandRepository;
import com.erdouglass.emdb.media.person.application.port.out.PersonDirectory;
import com.erdouglass.emdb.media.person.application.port.out.PersonStub;
import com.erdouglass.emdb.media.person.domain.model.Biography;
import com.erdouglass.emdb.media.person.domain.model.BirthDate;
import com.erdouglass.emdb.media.person.domain.model.DeathDate;
import com.erdouglass.emdb.media.person.domain.model.Name;
import com.erdouglass.emdb.media.person.domain.model.Person;
import com.erdouglass.emdb.media.person.domain.model.PersonDetails;
import com.erdouglass.emdb.media.person.domain.model.PersonId;
import com.erdouglass.emdb.media.person.domain.model.PersonPublicId;

@ApplicationScoped
class PersonCommandAdapter implements PersonCommandRepository, PersonDirectory {
  
  @Inject
  JakartaDataPersonCommandRepository repository;

  @Override
  public Person insert(Person person) {
    var entity = repository.insert(toPersonEntity(person)); 
    return toPerson(entity);
  }

  @Override
  public Person update(Person person) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<Person> findByTmdbId(TmdbId tmdbId) {
    return repository.findByTmdbIdId(tmdbId.value()).map(this::toPerson);
  }
  
  @Override
  public Map<TmdbId, PersonPublicId> register(Set<PersonStub> stubs) {
    var tmdbIds = stubs.stream().map(s -> s.tmdbId().value()).toList();
    var existing = repository.findByTmdbIdIn(tmdbIds).stream()
        .collect(Collectors.toMap(PersonEntity::getTmdbId, Function.identity()));
    var stubsToInsert = stubs.stream()
        .filter(c -> !existing.containsKey(c.tmdbId().value()))
        .map(this::toPersonEntity)
        .toList();
    for (var stub : repository.insertAll(stubsToInsert)) {
      existing.put(stub.getTmdbId(), stub);
    }
    return existing.entrySet().stream().collect(Collectors
        .toMap(e -> TmdbId.of(e.getKey()), e -> PersonPublicId.from(e.getValue().getId())));
  }
  
  private PersonEntity toPersonEntity(Person person) {
    var entity = new PersonEntity();
    entity.setId(person.publicId().map(PersonPublicId::toLong).orElse(null));
    entity.setSurrogateId(person.id().value());
    entity.setTmdbId(person.tmdbId().value());
    entity.setVersion(person.version().value());
    entity.setName(person.name().value());
    entity.setBirthDate(person.birthDate().map(BirthDate::toLocalDate).orElse(null));
    entity.setDeathDate(person.deathDate().map(DeathDate::toLocalDate).orElse(null));
    entity.setGender(person.gender().orElse(null));
    entity.setBiography(person.biography().map(Biography::value).orElse(null));
    return entity;
  }
  
  private PersonEntity toPersonEntity(PersonStub stub) {
    var entity = new PersonEntity();
    entity.setSurrogateId(PersonId.newId().value());
    entity.setTmdbId(stub.tmdbId().value());
    entity.setName(stub.name().value());
    return entity;
  }
  
  private Person toPerson(PersonEntity entity) {
    var id = PersonId.of(entity.getSurrogateId());
    var publicId = PersonPublicId.from(entity.getId());
    var tmdbId = TmdbId.of(entity.getTmdbId());
    var version = Version.of(entity.getVersion());
    var details = PersonDetails.builder()
        .name(Name.of(entity.getName()))
        .birthDate(entity.getBirthDate().map(BirthDate::from).orElse(null))
        .deathDate(entity.getDeathDate().map(DeathDate::from).orElse(null))
        .gender(entity.getGender().orElse(null))
        .biography(entity.getBiography().map(Biography::of).orElse(null))
        .build();
    return Person.rehydrate(id, publicId, tmdbId, version, details);
  }
}
