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

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.dto.SaveResult.SaveStatus;
import com.erdouglass.emdb.media.entity.Credit;
import com.erdouglass.emdb.media.entity.MovieCredit;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.entity.SeriesCredit;
import com.erdouglass.emdb.media.logging.LogDuration;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.repository.MovieCreditRepository;
import com.erdouglass.emdb.media.repository.PersonRepository;
import com.erdouglass.emdb.media.repository.SeriesCreditRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

@ApplicationScoped
public class PersonService {
  private static final String CREDITS = "credits";
  
  @Inject
  MovieCreditRepository movieCreditRepository;
  
  @Inject
  SeriesCreditRepository seriesCreditRepository;
  
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
      return SaveResult.of(SaveStatus.CREATED, insertedPerson);
    } else if (!isEqual(command, existingPerson)) {
      mapper.merge(command, existingPerson);
      var updatedPerson = repository.update(existingPerson);
      return SaveResult.of(SaveStatus.UPDATED, updatedPerson);
    }
    return SaveResult.of(SaveStatus.UNCHANGED, existingPerson);
  }
  
  @Transactional
  @LogDuration(value = "Saved:", subject = "people")
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
        results.add(SaveResult.of(SaveStatus.CREATED, person));
      } else if (!isEqual(command, existingPerson)) {        
        mapper.merge(command, existingPerson);
        peopleToUpdate.add(existingPerson);
        results.add(SaveResult.of(SaveStatus.UPDATED, existingPerson));
      } else {
        results.add(SaveResult.of(SaveStatus.UNCHANGED, existingPerson));
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
  @LogDuration("Found:")
  public Optional<Person> findById(@NotNull @Positive Long id, String append) {
    var person = repository.findById(id);
    person.ifPresent(p -> {
      p.setCredits(List.of());
      if (append != null && append.contains(CREDITS)) { 
        List<Credit> allCredits = new ArrayList<>();
        allCredits.addAll(movieCreditRepository.findByPersonId(id));
        allCredits.addAll(seriesCreditRepository.findByPersonId(id));
        allCredits.sort((c1, c2) -> {
          Float s1 = (c1 instanceof MovieCredit mc) ? mc.getMovie().getScore() : ((SeriesCredit) c1).getSeries().getScore();
          Float s2 = (c2 instanceof MovieCredit mc) ? mc.getMovie().getScore() : ((SeriesCredit) c2).getSeries().getScore();          
          if (s1 == null) return 1;
          if (s2 == null) return -1;
          return s2.compareTo(s1); 
        });    
        p.setCredits(allCredits);
      }
    });
    return person;
  }
  
  @Transactional
  @LogDuration("Found:")
  public Optional<Person> findByTmdbId(@NotNull @Positive Integer id) {
    return repository.findByTmdbId(id);
  }
  
  @Transactional
  @LogDuration("Updated:")
  public Person update(Long id, UpdatePerson command) {
    var existingPerson = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No person found with id: " + id));
    mapper.merge(command, existingPerson);
    return repository.update(existingPerson);
  } 
  
  @Transactional
  @LogDuration(value = "Deleted:", subject = "person")
  public void delete(Long id) {
    repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No person found with id: " + id));
    repository.deleteById(id);
  }
  
  private boolean isEqual(SavePerson command, Person person) {
    if (command == null || person == null) return false;
    return Objects.equals(command.tmdbId(), person.getTmdbId())
        && Objects.equals(command.name(), person.getName())
        && Objects.equals(command.birthDate(), person.getBirthDate())
        && Objects.equals(command.deathDate(), person.getDeathDate())
        && Objects.equals(command.gender(), person.getGender())
        && Objects.equals(command.profile() != null ? command.profile().name() : null, person.getProfile())
        && Objects.equals(command.birthPlace(), person.getBirthPlace())
        && Objects.equals(command.biography(), person.getBiography());
  }

}
