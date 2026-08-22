package com.erdouglass.emdb.media.person.application.port.in;

import java.util.Optional;

import com.erdouglass.emdb.media.person.application.port.out.PersonView;
import com.erdouglass.emdb.media.person.domain.PersonPublicId;

/// Inbound port for reading a single person.
///
/// The read half of the CQRS split: implementations project from the database
/// rather than loading the aggregate, so nothing here can be used to mutate and
/// no domain invariant is re-applied on the way out.
public interface FindPersonUseCase {

  /// Looks up a person by its catalogue id.
  ///
  /// Absence is a normal result, not a failure — the caller decides whether it
  /// means a `404`, a null GraphQL field, or something else.
  ///
  /// @param id the catalogue id
  /// @return the projected person, or empty if none carries that id
  Optional<PersonView> findById(PersonPublicId id);
}