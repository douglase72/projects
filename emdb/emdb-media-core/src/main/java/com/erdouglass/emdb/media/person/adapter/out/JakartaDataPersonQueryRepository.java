package com.erdouglass.emdb.media.person.adapter.out;

import java.util.Optional;

import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.person.application.port.out.PersonView;

/// Jakarta Data repository for the read side of the person table.
///
/// Projects straight into the client-facing view rather than loading entities,
/// so a read touches only the columns it returns and never materializes an
/// aggregate. Internal columns — the surrogate id, the TMDB id, the lock flag —
/// are absent from the projection by design.
@Repository(dataStore = "media")
interface JakartaDataPersonQueryRepository {

  /// Projects a single person by primary key.
  ///
  /// The selected columns line up positionally with the raw-value constructor of
  /// the view, which converts the numeric key into the prefixed catalogue id.
  /// Reordering the select list without reordering that constructor will bind
  /// the wrong columns.
  ///
  /// @param id the numeric primary key
  /// @return the projected person, or empty if none carries that key
  @Query("""
    select p.id, p.version, p.name, p.birthDate, p.deathDate, p.gender, p.biography
    from PersonEntity p
    where p.id = :id          
          """)
  Optional<PersonView> findById(Long id);
}
