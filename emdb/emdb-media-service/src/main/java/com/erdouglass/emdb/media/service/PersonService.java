package com.erdouglass.emdb.media.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.repository.PersonRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class PersonService {
  private static final Logger LOGGER = Logger.getLogger(PersonService.class);
  
  @Inject
  PersonRepository repository;
  
  @Transactional
  public Person create(@NotNull @Valid Person person) {
    var newPerson = repository.insert(person);
    LOGGER.infof("Created: %s", newPerson);   
    return newPerson;
  }
  
  @Transactional
  public Person findById(Long id) {
    var person = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
    LOGGER.infof("Found: %s", person);
    return person;
  }

}
