package com.erdouglass.emdb.media.person.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import com.erdouglass.emdb.media.person.application.port.out.PersonView;

@Repository(dataStore = "media")
interface JakartaDataPersonQueryRepository {

  @Query("""
      select p.id, p.version, p.name, p.birthDate, p.deathDate, p.gender, p.biography
      from PersonEntity p
      where p.id = :id          
            """)
    Optional<PersonView> findById(UUID id);
}
