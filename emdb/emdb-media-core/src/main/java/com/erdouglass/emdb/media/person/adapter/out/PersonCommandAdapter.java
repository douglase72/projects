package com.erdouglass.emdb.media.person.adapter.out;

import java.util.Optional;

import jakarta.data.exceptions.OptimisticLockingFailureException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.person.application.port.out.PersonCommandRepository;
import com.erdouglass.emdb.media.person.domain.Biography;
import com.erdouglass.emdb.media.person.domain.BirthDate;
import com.erdouglass.emdb.media.person.domain.DeathDate;
import com.erdouglass.emdb.media.person.domain.Name;
import com.erdouglass.emdb.media.person.domain.Person;
import com.erdouglass.emdb.media.person.domain.PersonDetails;
import com.erdouglass.emdb.media.person.domain.PersonId;
import com.erdouglass.emdb.media.person.domain.PersonPublicId;

/// Translates between the person aggregate and its persistent row.
///
/// The seam that keeps JPA out of the domain: everything above this class works
/// in aggregates and value objects, everything below in entities and column
/// types. Validation runs in both directions as a side effect of that
/// translation, since rebuilding value objects re-applies their invariants — a
/// row that violates them fails loudly on load rather than propagating.
///
/// Holds no transaction of its own; callers supply one.
@ApplicationScoped
class PersonCommandAdapter implements PersonCommandRepository {
  
  @Inject
  JakartaDataPersonCommandRepository repository;

  /// Writes a new person and returns it with its database-assigned identity.
  ///
  /// The returned aggregate is not the one passed in: it carries the public id
  /// and version the database supplied, which the argument could not have had.
  /// Callers must use the return value for anything that needs those, including
  /// audit attribution.
  ///
  /// @param person the unpersisted aggregate
  /// @return the persisted aggregate, now reporting a public id and version
  @Override
  public Person insert(Person person) {
    return toPerson(repository.insert(toPersonEntity(person)));
  }

  /// Writes a modified person, checking the optimistic-locking version.
  ///
  /// @param person the aggregate to persist, carrying the version it was read at
  /// @return the persisted aggregate with the incremented version
  /// @throws OptimisticLockingFailureException if the stored version has moved
  ///         on since the aggregate was loaded
  @Override
  public Person update(Person person) {
    return toPerson(repository.update(toPersonEntity(person)));
  }

  /// Removes the person with the given catalogue id.
  ///
  /// Deleting an id that no longer exists is not reported as an error here; the
  /// service checks existence before calling, so it can produce a `404` and can
  /// close out the audit trail first.
  ///
  /// @param publicId the catalogue id of the title to remove
  @Override
  public void deleteByPublicId(PersonPublicId publicId) {
    repository.deleteById(publicId.toLong());
  }

  /// Loads a person by its catalogue id.
  ///
  /// @param publicId the catalogue id
  /// @return the rehydrated aggregate, or empty if none carries that id
  /// @throws IllegalArgumentException if a stored value no longer satisfies its
  ///         domain invariants
  @Override
  public Optional<Person> findByPublicId(PersonPublicId publicId) {
    return repository.findById(publicId.toLong()).map(this::toPerson);
  }

  /// Loads a person by its natural id, the lookup that turns ingestion into an
  /// upsert.
  ///
  /// @param tmdbId the natural id from the upstream catalogue
  /// @return the rehydrated aggregate, or empty if the title is new
  /// @throws IllegalArgumentException if a stored value no longer satisfies its
  ///         domain invariants
  @Override
  public Optional<Person> findByTmdbId(TmdbId tmdbId) {
    return repository.findByTmdbId(tmdbId.value()).map(this::toPerson);
  }
  
  /// Flattens an aggregate into a row.
  ///
  /// Two conversions carry meaning. An aggregate with no public id yields a row
  /// with a `null` key, which is what tells the provider to insert rather than
  /// update. An aggregate with no version yields `0`, the version a row is born
  /// with.
  ///
  /// @param person the aggregate to flatten
  /// @return the row to write
  private PersonEntity toPersonEntity(Person person) {
    var entity = new PersonEntity();
    entity.setId(person.publicId().map(PersonPublicId::toLong).orElse(null));
    entity.setSurrogateId(person.id().value());
    entity.setTmdbId(person.tmdbId().value());
    entity.setVersion(person.version().map(Version::value).orElse(0L));
    entity.setLocked(person.isLocked());
    entity.setName(person.details().name().value());
    entity.setBirthDate(person.details().birthDate().map(BirthDate::toLocalDate).orElse(null));
    entity.setDeathDate(person.details().deathDate().map(DeathDate::toLocalDate).orElse(null));
    entity.setGender(person.details().gender());
    entity.setBiography(person.details().biography().map(Biography::value).orElse(null));
    return entity;
  }
  
  /// Rebuilds an aggregate from a row.
  ///
  /// Goes through `Person.rehydrate` rather than the creation factory, so a
  /// locked title can be loaded and its stored identity is preserved rather than
  /// regenerated. Every column passes back through its value object, so an
  /// invalid row surfaces here.
  ///
  /// @param entity the row to rebuild from
  /// @return the aggregate
  /// @throws IllegalArgumentException if a stored value violates a domain invariant
  private Person toPerson(PersonEntity entity) {
    var id = PersonId.of(entity.getSurrogateId());
    var publicId = PersonPublicId.from(entity.getId());
    var tmdbId = TmdbId.of(entity.getTmdbId());
    var details = PersonDetails.builder()
        .name(Name.of(entity.getName()))
        .birthDate(entity.getBirthDate().map(BirthDate::from).orElse(null))
        .deathDate(entity.getDeathDate().map(DeathDate::from).orElse(null))
        .gender(entity.getGender())
        .biography(entity.getBiography().map(Biography::of).orElse(null))
        .build();
    return Person.rehydrate(id, publicId, tmdbId, entity.getLocked(), details, Version.of(entity.getVersion()));
  }
}
