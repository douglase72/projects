package com.erdouglass.emdb.media.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.request.PersonUpdateRequest;
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
  
  @Transactional
  public Person update(@NotNull @Positive Long id, @NotNull @Valid PersonUpdateRequest request) {
    var person = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No person found with id: " + id));
    request.name().ifPresent(person::name);
    request.birthDate().ifPresent(person::birthDate);
    request.deathDate().ifPresent(person::deathDate);
    request.gender().ifPresent(person::gender);
    request.birthPlace().ifPresent(person::birthPlace);
    request.profile().ifPresent(person::profile);
    request.biography().ifPresent(person::biography);
    var updatedPerson = repository.update(person);   
    LOGGER.infof("Updated: %s", updatedPerson);
    return updatedPerson;
  }
  
  @Transactional
  public void delete(@NotNull @Positive Long id) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No person found with id: " + id));
    repository.deleteById(id);
    LOGGER.infof("Deleted: %s", movie);
  }

}
