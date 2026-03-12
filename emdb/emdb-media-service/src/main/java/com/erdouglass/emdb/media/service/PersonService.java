package com.erdouglass.emdb.media.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.logging.LogDuration;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.repository.PersonRepository;

@ApplicationScoped
public class PersonService {
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonRepository repository;
  
  @Transactional
  @LogDuration("Saved:")
  public Person save(SavePerson command) {
    Person savedPerson;
    var existingPerson = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existingPerson == null) {
      savedPerson = repository.insert(mapper.toPerson(command));
    } else {
      mapper.merge(command, existingPerson);
      savedPerson = repository.update(existingPerson);
    }
    return savedPerson; 
  }

}
