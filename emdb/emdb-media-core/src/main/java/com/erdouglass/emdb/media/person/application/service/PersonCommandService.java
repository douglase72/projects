package com.erdouglass.emdb.media.person.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.person.SavePersonCommand;
import com.erdouglass.emdb.media.person.SavePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.out.PersonAuditRepository;
import com.erdouglass.emdb.media.person.application.port.out.PersonCommandRepository;
import com.erdouglass.emdb.media.person.domain.Person;
import com.erdouglass.emdb.media.person.domain.PersonDetails;
import com.erdouglass.emdb.media.person.domain.PersonId;
import com.erdouglass.emdb.media.person.domain.PersonPublicId;
import com.erdouglass.emdb.media.person.domain.exception.LockedPersonException;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

/// Orchestrates every write to the person catalogue.
///
/// Implements all four write ports together because they share one recurring
/// shape — load, check, mutate, persist, audit — and splitting them would
/// duplicate that sequence four times. The rules themselves live on the
/// aggregate; this class only sequences them and owns the transaction.
///
/// Auditing is not optional and not a listener: each method appends the changes
/// it caused inside the same transaction, so a write cannot commit without its
/// trail.
///
/// Surrogate ids are generated here as time-ordered UUIDv7 values, giving a
/// person identity before it reaches the database.
///
/// Package-private and stateless beyond its injected collaborators; the CDI
/// container hands it out through the port interfaces.
@ApplicationScoped
class PersonCommandService implements SavePersonUseCase{
  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();
  private static final Logger LOGGER = Logger.getLogger(PersonCommandService.class);
  
  @Inject
  PersonAuditRepository audit;
  
  @Inject
  PersonCommandRepository people;
  
  /// Ingests a person, inserting it if the natural id is new.
  ///
  /// The lookup by TMDB id is what makes this an upsert. No version is checked,
  /// so an ingestion run never fails as stale — the trade is that a concurrent
  /// edit can be overwritten, which is why interactive clients use
  /// [#update(UpdatePersonCommand)] instead.
  ///
  /// @param command the complete intended state of the person
  /// @return the catalogue id, the version afterwards, and which outcome occurred
  /// @throws LockedPersonException if the title exists and is locked, including
  ///         when the incoming details are identical
  @Override
  @Transactional
  public SaveResult save(SavePersonCommand command) {
    var details = PersonMapper.toPersonDetails(command);
    return people.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, details))
        .orElseGet(() -> insert(command.tmdbId(), details));
  }
  
  /// Creates and persists a person that the catalogue has not seen before.
  ///
  /// The audit trail is seeded from the *persisted* aggregate rather than the
  /// one just built, because only the persisted one knows the catalogue id the
  /// database assigned.
  ///
  /// @param tmdbId the natural id of the new title
  /// @param details the initial details
  /// @return the catalogue id and initial version, reported as a creation
  private SaveResult insert(TmdbId tmdbId, PersonDetails details) {    
    var person = Person.create(PersonId.of(GENERATOR.generate()), tmdbId, details);
    var inserted = people.insert(person);
    audit.append(inserted.id(), inserted.publicId().orElseThrow(), inserted.changesAsAdded());
    LOGGER.infof("Created: %s", inserted);
    return SaveResult.of(
        inserted.publicId().map(PersonPublicId::value).orElseThrow(), 
        inserted.version().map(Version::value).orElseThrow(), 
        Status.CREATED); 
  }
  
  /// Applies details to an existing person and persists only if something moved.
  ///
  /// Shared by both write paths, which is why it takes an already-existing
  /// aggregate: each caller has its own way of finding the person and its own
  /// pre-checks, and only the part after that is common.
  ///
  /// The early return on an empty diff is what makes ingestion cheap to replay —
  /// a no-op run touches neither the person table nor the audit table, and does
  /// not advance the version.
  ///
  /// @param person the loaded aggregate, already version-checked if the caller
  ///        requires it
  /// @param target the complete intended details
  /// @return the catalogue id and version, reported as unchanged when the
  ///         details already matched
  /// @throws LockedPersonException if the title is locked, raised before the diff
  ///         is taken and therefore even when nothing would have changed
  private SaveResult update(Person person, PersonDetails target) {
    var changes = person.update(target);
    if (changes.isEmpty()) {
      return SaveResult.of(
          person.publicId().map(PersonPublicId::value).orElseThrow(), 
          person.version().map(Version::value).orElseThrow(), 
          Status.UNCHANGED);
    }    
    var updated = people.update(person);
    audit.append(updated.id(), updated.publicId().orElseThrow(), changes);
    LOGGER.infof("Updated: %s", updated);
    return SaveResult.of(
        updated.publicId().map(PersonPublicId::value).orElseThrow(), 
        updated.version().map(Version::value).orElseThrow(), 
        Status.UPDATED);
  }
}
