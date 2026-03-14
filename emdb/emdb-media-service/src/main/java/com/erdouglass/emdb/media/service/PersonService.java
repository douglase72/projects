package com.erdouglass.emdb.media.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.dto.SaveResult.Status;
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
  public SaveResult<Person> save(@NotNull @Valid SavePerson command) {
    var existingPerson = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existingPerson == null) {
      var insertedPerson = repository.insert(mapper.toPerson(command));
      return SaveResult.of(Status.CREATED, insertedPerson);
    } else if (!isEqual(command, existingPerson)) {
      mapper.merge(command, existingPerson);
      var updatedPerson = repository.update(existingPerson);
      return SaveResult.of(Status.UPDATED, updatedPerson);
    }
    return SaveResult.of(Status.UNCHANGED, existingPerson);
  }
  
  @Transactional
  @LogDuration("Saved:")
  public List<SaveResult<Person>> saveAll(@NotEmpty List<@Valid SavePerson> commands) {
    List<SaveResult<Person>> results = new ArrayList<>();
    List<Person> peopleToInsert = new ArrayList<>();
    List<Person> peopleToUpdate = new ArrayList<>();
    var tmdbIds = commands.stream().map(SavePerson::tmdbId).toList();
    var existingPeople = repository.findByTmdbIdIn(tmdbIds).stream()
        .collect(Collectors.toMap(Person::getTmdbId, Function.identity()));    
    
    for (var command : commands) {
      var existingPerson = existingPeople.get(command.tmdbId());
      if (existingPerson == null) {
        var person = mapper.toPerson(command);
        peopleToInsert.add(person);
        results.add(SaveResult.of(Status.CREATED, person));
      } else if (!isEqual(command, existingPerson)) {        
        mapper.merge(command, existingPerson);
        peopleToUpdate.add(existingPerson);
        results.add(SaveResult.of(Status.UPDATED, existingPerson));
      } else {
        results.add(SaveResult.of(Status.UNCHANGED, existingPerson));
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
  
  private boolean isEqual(SavePerson command, Person person) {
    if (command == null || person == null) return false;
    return Objects.equals(command.tmdbId(), person.getTmdbId())
        && Objects.equals(command.name(), person.getName())
        && Objects.equals(command.birthDate(), person.getBirthDate())
        && Objects.equals(command.deathDate(), person.getDeathDate())
        && Objects.equals(command.gender(), person.getGender())
        && Objects.equals(command.profile(), person.getProfile())
        && Objects.equals(command.birthPlace(), person.getBirthPlace())
        && Objects.equals(command.biography(), person.getBiography());
  }

}
