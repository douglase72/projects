package com.erdouglass.emdb.media.person.application.port.out;

import java.util.List;
import java.util.Optional;

import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.person.domain.model.Person;

public interface PersonCommandRepository {
  
  Person insert(Person person);
  
  List<Person> insertAll(List<Person> people);
  
  Person update(Person person);
  
  Optional<Person> findByTmdbId(TmdbId tmdbId);
  
  List<Person> findByTmdbIdIn(List<TmdbId> tmdbIds);
}
