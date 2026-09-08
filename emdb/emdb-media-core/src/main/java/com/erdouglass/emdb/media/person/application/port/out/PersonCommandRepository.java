package com.erdouglass.emdb.media.person.application.port.out;

import java.util.Optional;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.person.domain.model.Person;

public interface PersonCommandRepository {

  Person insert(Person person);
  
  Person update(Person person);
  
  Optional<Person> findById(PublicId id);
  
  Optional<Person> findByTmdbId(TmdbId tmdbId);
}
