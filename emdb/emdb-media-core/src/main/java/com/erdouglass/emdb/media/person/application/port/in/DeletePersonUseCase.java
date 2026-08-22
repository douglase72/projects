package com.erdouglass.emdb.media.person.application.port.in;

import com.erdouglass.emdb.media.person.domain.PersonPublicId;
import com.erdouglass.emdb.media.person.domain.exception.PersonNotFoundException;

/// Inbound port for removing a person from the catalogue.
///
/// Deliberately versionless: a delete has no state to merge, so refusing one as
/// stale would only force a re-read before repeating the same request. A locked
/// person is likewise no obstacle — the lock guards details, not existence.
public interface DeletePersonUseCase {

  /// Removes the person, recording its final state in the audit trail first.
  ///
  /// The trail is closed out before the row goes, so the persons's history ends
  /// with every populated field marked as removed rather than simply stopping.
  ///
  /// Not idempotent: deleting an id twice fails the second time, so that a
  /// client working from a stale list learns its view is out of date.
  ///
  /// @param id the catalogue id of the person to remove
  /// @throws PersonNotFoundException if no person carries `id`
  void delete(PersonPublicId id);
}
