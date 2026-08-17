package com.erdouglass.emdb.media.person.application.port.out;

import java.util.Optional;

import com.erdouglass.emdb.media.person.domain.PersonPublicId;

/// Outbound port for client-facing reads.
///
/// Returns the view rather than the aggregate, which is the point: a read served
/// through this port cannot be used to mutate, and implementations are free to
/// project, denormalise or cache without the domain noticing.
public interface PersonQueryRepository {

  /// Reads a single person by catalogue id.
  ///
  /// @param id the catalogue id
  /// @return the projected person, or empty if none carries that id
  Optional<PersonView> findById(PersonPublicId id);
}
