package com.erdouglass.emdb.media.person.application.port.out;

import java.util.Optional;

import jakarta.data.exceptions.OptimisticLockingFailureException;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.person.domain.Person;
import com.erdouglass.emdb.media.person.domain.PersonPublicId;

/// Outbound port for loading and storing person aggregates.
///
/// The write side's whole view of persistence. Both lookups exist because the
/// two write paths address people differently — ingestion by natural id, editing
/// by catalogue id — and both return aggregates rather than projections, since
/// their results are about to be modified.
///
/// Implementations are expected to run in the caller's transaction.
public interface PersonCommandRepository {

  /// Persists a new person.
  ///
  /// The returned aggregate carries the identity the store assigned, which the
  /// argument could not have had. Callers must use the return value for
  /// anything downstream, including audit attribution.
  ///
  /// @param movie the unpersisted aggregate
  /// @return the persisted aggregate, now reporting a public id and version
  Person insert(Person person);
  
  /// Persists changes to an existing person, checking the version.
  ///
  /// @param movie the aggregate to write, carrying the version it was loaded at
  /// @return the persisted aggregate with the incremented version
  /// @throws OptimisticLockingFailureException if the stored version has moved
  ///         on since the aggregate was loaded
  Person update(Person person);
  
  /// Removes the title with the given catalogue id.
  ///
  /// Removing an id that is already gone is not required to fail; callers that
  /// need a distinct outcome check existence first.
  ///
  /// @param publicId the catalogue id of the title to remove
  void deleteByPublicId(PersonPublicId publicId);
  
  /// Loads a title by catalogue id, for editing.
  ///
  /// @param publicId the catalogue id
  /// @return the aggregate, or empty if none carries that id
  Optional<Person> findByPublicId(PersonPublicId publicId);
  
  /// Loads a title by natural id — the lookup that decides whether ingestion
  /// inserts or updates.
  ///
  /// @param tmdbId the natural id from the upstream catalogue
  /// @return the aggregate, or empty if the title has not been ingested
  Optional<Person> findByTmdbId(TmdbId tmdbId);
}
