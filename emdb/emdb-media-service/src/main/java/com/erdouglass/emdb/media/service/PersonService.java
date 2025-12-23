package com.erdouglass.emdb.media.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.request.PersonUpdateRequest;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.entity.Person_;
import com.erdouglass.emdb.media.query.PersonStatus;
import com.erdouglass.emdb.media.query.StatusCode;
import com.erdouglass.emdb.media.repository.CreditRepository;
import com.erdouglass.emdb.media.repository.PersonRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class PersonService {
  private static final Logger LOGGER = Logger.getLogger(PersonService.class);
  
  @Inject
  CreditRepository creditRepository;
  
  @Inject
  PersonRepository repository;
  
  @Transactional
  public Person create(@NotNull @Valid Person person) {
    var newPerson = repository.insert(person);
    LOGGER.infof("Created: %s", newPerson);   
    return newPerson;
  }
  
  @Transactional
  public List<PersonStatus> createAll(@NotEmpty List<@Valid Person> people) {
    var results = insert(people);
    LOGGER.infof("Created: %d people", results.stream()
            .filter(r -> r.statusCode() == StatusCode.CREATED)
            .count());
    return results;
  }
  
  @Transactional
  public Person findById(Long id, String append) {
    var person = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
    person.credits(List.of());
    if (append != null) {
      if (append.contains(Person_.CREDITS)) {
        person.credits(creditRepository.findAll(id));
      }
    }    
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
  
  private List<PersonStatus> insert(List<Person> people) {
    var results = new ArrayList<PersonStatus>();
    var peopleToInsert = new ArrayList<Person>();
    var tmdbIds = people.stream().map(Person::tmdbId).toList();
    var existingPeople = repository.findByTmdbIdIn(tmdbIds).stream()
        .collect(Collectors.toMap(Person::tmdbId, Function.identity())); 
    
    for (var person : people) {
      if (existingPeople.containsKey(person.tmdbId())) {
        results.add(new PersonStatus(existingPeople.get(person.tmdbId()), StatusCode.UNCHANGED));
      } else {
        peopleToInsert.add(person);
        results.add(new PersonStatus(person, StatusCode.CREATED));
      }
    }
    
    if (!peopleToInsert.isEmpty()) {
      repository.insertAll(peopleToInsert);
    }
    return results;
  }

}
