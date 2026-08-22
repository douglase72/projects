package com.erdouglass.emdb.media.person.application.port.out;

import java.util.List;

import com.erdouglass.emdb.media.person.domain.PersonFieldChange;
import com.erdouglass.emdb.media.person.domain.PersonId;
import com.erdouglass.emdb.media.person.domain.PersonPublicId;

/// Outbound port for the audit trail.
///
/// Append-only: the port offers no way to read, amend or remove history, so no
/// implementation can be asked to. Reading the trail is a reporting concern and
/// is not modelled here.
public interface PersonAuditRepository {

  /// Records a set of field changes against a person.
  ///
  /// Both identifiers are taken because they serve different purposes — the
  /// surrogate id attributes history durably, the catalogue id keeps it legible.
  ///
  /// Expected to enlist in the caller's transaction, so that history and the
  /// change it describes commit together or not at all.
  ///
  /// @param id the surrogate id of the changed person
  /// @param publicId the catalogue id as it stood at the time of the change
  /// @param changes the differences to record; an empty list records nothing
  void append(PersonId id, PersonPublicId publicId, List<PersonFieldChange> changes);
}
