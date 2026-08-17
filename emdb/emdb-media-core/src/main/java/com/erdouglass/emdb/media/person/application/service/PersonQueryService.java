package com.erdouglass.emdb.media.person.application.service;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.person.application.port.in.FindPersonUseCase;
import com.erdouglass.emdb.media.person.application.port.out.PersonQueryRepository;
import com.erdouglass.emdb.media.person.application.port.out.PersonView;
import com.erdouglass.emdb.media.person.domain.PersonPublicId;

/// Serves client reads of the person catalogue.
///
/// Deliberately a pass-through. The read side has no rules to enforce — no
/// aggregate to load, no version to check, no lock to respect — so the class
/// exists to keep the port and the adapter from depending on one another, not to
/// add behaviour. Resist the pull to put filtering or shaping here; that belongs
/// in the projection.
///
/// Carries no transaction: reads run in whatever context the caller provides, or
/// in none.
@ApplicationScoped
class PersonQueryService implements FindPersonUseCase {
  
  @Inject
  PersonQueryRepository query;

  /// Looks up a person by its catalogue id.
  ///
  /// @param id the catalogue id
  /// @return the projected person, or empty if none carries that id
  @Override
  public Optional<PersonView> findById(PersonPublicId id) {
    return query.findById(id);
  }
}
