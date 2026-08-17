package com.erdouglass.emdb.media.person.adapter.out;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.person.application.port.out.PersonQueryRepository;
import com.erdouglass.emdb.media.person.application.port.out.PersonView;
import com.erdouglass.emdb.media.person.domain.PersonPublicId;

/// Serves client reads by projecting straight from the database.
///
/// The counterpart to [PersonCommandAdapter], and deliberately thinner: there is
/// no aggregate to rebuild and no invariant to re-apply, so the class only
/// converts the catalogue id into a database key and hands back what the query
/// produced. A read here loads no entity and takes no lock.
@ApplicationScoped
class PersonQueryAdapter implements PersonQueryRepository {
  
  @Inject
  JakartaDataPersonQueryRepository repository;

  /// Projects a single person by its catalogue id.
  ///
  /// @param id the catalogue id, e.g. `pr_42`
  /// @return the projected person, or empty if none carries that id
  @Override
  public Optional<PersonView> findById(PersonPublicId id) {
    return repository.findById(id.toLong());
  }
}
