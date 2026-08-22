package com.erdouglass.emdb.media.person.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.Result;
import com.erdouglass.emdb.media.Result.Status;
import com.erdouglass.emdb.media.SourceId;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.person.SavePersonCommand;
import com.erdouglass.emdb.media.person.SavePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.in.DeletePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.in.UpdatePersonCommand;
import com.erdouglass.emdb.media.person.application.port.in.UpdatePersonUseCase;
import com.erdouglass.emdb.media.person.application.port.out.PersonAuditRepository;
import com.erdouglass.emdb.media.person.application.port.out.PersonCommandRepository;
import com.erdouglass.emdb.media.person.domain.Person;
import com.erdouglass.emdb.media.person.domain.PersonDetails;
import com.erdouglass.emdb.media.person.domain.PersonId;
import com.erdouglass.emdb.media.person.domain.PersonPublicId;
import com.erdouglass.emdb.media.person.domain.exception.PersonNotFoundException;
import com.erdouglass.emdb.media.person.domain.exception.StalePersonException;
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
class PersonCommandService implements SavePersonUseCase, UpdatePersonUseCase, DeletePersonUseCase {
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
  public Result save(SavePersonCommand command) {
    var details = PersonMapper.toPersonDetails(command);
    return people.findBySourceId(command.sourceId())
        .map(existing -> update(existing, details))
        .orElseGet(() -> insert(command.sourceId(), details));
  }
  
  /// Edits an existing person, refusing the write if the caller's version is
  /// stale.
  ///
  /// The version is checked before the details are applied, so a stale request
  /// leaves the aggregate untouched and writes no audit rows.
  ///
  /// @param command the intended state, target id and the version the caller read
  /// @return the catalogue id, the version afterwards, and which outcome occurred
  /// @throws PersonNotFoundException if no person carries the command's id
  /// @throws StalePersonException if the stored version differs from the supplied
  ///         one
  /// @throws LockedPersonException if the person[] is locked
  /// @throws IllegalArgumentException if the command's id is malformed
  @Override
  @Transactional
  public Result update(UpdatePersonCommand command) {
    var details = PersonMapper.toPersonDetails(command);
    Person existing = people.findByPublicId(PersonPublicId.of(command.publicId()))
        .orElseThrow(() -> new PersonNotFoundException(command.publicId()));
    existing.checkVersion(Version.of(command.version()));
    return update(existing, details);
  }
  
  /// Removes a person, closing out its history first.
  ///
  /// The load is what makes the audit possible: the person has to be read before
  /// it can be described as removed. Both writes share the transaction, so
  /// history cannot be recorded for a delete that then rolls back.
  ///
  /// Neither the version nor the lock is checked — deletion is not a content
  /// change, and there is no state to merge.
  ///
  /// @param id the catalogue id of the title to remove
  /// @throws PersonNotFoundException if no title carries `id`
  @Override
  @Transactional
  public void delete(PersonPublicId id) {
    Person person = people.findByPublicId(id)
        .orElseThrow(() -> new PersonNotFoundException(id.value())); 
    audit.append(person.id(), person.publicId().orElseThrow(), person.changesAsDeleted());
    people.deleteByPublicId(id);
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
  private Result insert(SourceId sourceId, PersonDetails details) {    
    var person = Person.create(PersonId.of(GENERATOR.generate()), sourceId, details);
    var inserted = people.insert(person);
    audit.append(inserted.id(), inserted.publicId().orElseThrow(), inserted.changesAsAdded());
    LOGGER.infof("Created: %s", inserted);
    return Result.of(
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
  private Result update(Person person, PersonDetails target) {
    var changes = person.update(target);
    if (changes.isEmpty()) {
      return Result.of(
          person.publicId().map(PersonPublicId::value).orElseThrow(), 
          person.version().map(Version::value).orElseThrow(), 
          Status.UNCHANGED);
    }    
    var updated = people.update(person);
    audit.append(updated.id(), updated.publicId().orElseThrow(), changes);
    LOGGER.infof("Updated: %s", updated);
    return Result.of(
        updated.publicId().map(PersonPublicId::value).orElseThrow(), 
        updated.version().map(Version::value).orElseThrow(), 
        Status.UPDATED);
  }
}