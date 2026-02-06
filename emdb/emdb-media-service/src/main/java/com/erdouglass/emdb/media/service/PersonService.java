package com.erdouglass.emdb.media.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.media.dto.PersonStatus;
import com.erdouglass.emdb.media.dto.PersonStatus.Status;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.repository.PersonRepository;
import com.erdouglass.emdb.scraper.service.TmdbPersonScraper;

@ApplicationScoped
public class PersonService extends MediaService {
  private static final Logger LOGGER = Logger.getLogger(PersonService.class);
  
  @Inject
  TmdbPersonScraper personScraper;
  
  @Inject
  PersonRepository repository;
  
  @Override
  public void ingest(@NotNull @Positive Integer tmdbId, String jobId) {
    var existingPerson = findByTmdbId(tmdbId);
    var command = existingPerson
        .map(personMapper::toSavePerson)
        .orElseGet(() -> SavePerson.builder().tmdbId(tmdbId).build());
    var saveCommand = personScraper.scrape(command, jobId);
    var person = personService.save(personMapper.toPerson(saveCommand));
    LOGGER.infof("Saved: %s", person);
    existingPerson.ifPresent(p -> {
      if (!Objects.equals(p.tmdbProfile().orElse(null), person.tmdbProfile().orElse(null))) {
          p.profile().ifPresent(imageService::delete);
      }
    });      
  }
  
  @Transactional
  public Person save(@NotNull @Valid Person person) {
    repository.findByTmdbId(person.tmdbId()).ifPresent(p -> person.id(p.id()));
    var savedPerson = repository.save(person);
    return savedPerson;  
  }
  
  @Transactional
  public List<PersonStatus> saveAll(@NotEmpty List<@Valid Person> people) {
    List<PersonStatus> results = new ArrayList<>();
    List<Person> peopleToInsert = new ArrayList<>();
    List<Person> peopleToUpdate = new ArrayList<>();
    var tmdbIds = people.stream().map(Person::tmdbId).toList();
    var existingPeople = repository.findByTmdbIdIn(tmdbIds).stream()
        .collect(Collectors.toMap(Person::tmdbId, Function.identity()));
    
    for (var person : people) {
      var existingPerson = existingPeople.get(person.tmdbId());
      if (existingPerson == null) {
        peopleToInsert.add(person);
        results.add(PersonStatus.of(person, Status.CREATED));
      } else if (!existingPerson.isEqualTo(person)) {
        person.id(existingPerson.id());
        peopleToUpdate.add(person);
        results.add(PersonStatus.of(person, Status.UPDATED));
      } else {
        results.add(PersonStatus.of(person, Status.UNCHANGED));
      }
    }
    
    if (!peopleToInsert.isEmpty()) {
      repository.insertAll(peopleToInsert);
    }
    if (!peopleToUpdate.isEmpty()) {
      repository.updateAll(peopleToUpdate);
    }
    return results;
  }  
  
  @Transactional
  public Optional<Person> findById(@NotNull @Positive Long id, String append) {
    return repository.findById(id);
  }
  
  @Transactional
  Optional<Person> findByTmdbId(@NotNull @Positive Integer id) {
    return repository.findByTmdbId(id);
  }
  
}
