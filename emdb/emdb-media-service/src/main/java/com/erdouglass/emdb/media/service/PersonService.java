package com.erdouglass.emdb.media.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.common.query.PersonQueryParameters;
import com.erdouglass.emdb.common.query.PersonView;
import com.erdouglass.emdb.media.annotation.Logged;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.dto.SaveResult.SaveStatus;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.repository.PersonRepository;
import com.erdouglass.webservices.ResourceNotFoundException;

/// Service layer for person domain operations.
///
/// Handles create, read, update, and delete operations for people.
/// Write operations use TMDB ID as the natural key for upsert logic:
/// a person is created if no match is found, updated if fields differ,
/// or left unchanged if already current.
@ApplicationScoped
public class PersonService {

  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonRepository repository;
  
  /// Creates or updates a single person matched by TMDB ID.
  ///
  /// @param command the person data to save
  /// @return a [SaveResult] containing the save status and the persisted entity  
  @Transactional
  @Logged("Saved:")
  public SaveResult<Person> save(@NotNull @Valid SavePerson command) {
    var status = SaveStatus.UNCHANGED;
    var existingPerson = repository.findByTmdbId(command.tmdbId()).orElse(null);
    if (existingPerson == null) {
      existingPerson = repository.insert(mapper.toPerson(command));
      status = SaveStatus.CREATED;
    } else if (!isEqual(command, existingPerson)) {
      mapper.merge(command, existingPerson);
      repository.update(existingPerson);
      status = SaveStatus.UPDATED;
    }
    return SaveResult.of(status, existingPerson);
  }

  /// Creates or updates a batch of people in a single transaction.
  ///
  /// Pre-fetches all existing people by TMDB ID to minimize database
  /// round trips, then partitions the commands into inserts and updates
  /// for bulk persistence.
  ///
  /// @param commands the list of people to save
  /// @return a [SaveResult] per person in the same order as the input  
  @Transactional
  @Logged(value = "Saved:", subject = "people")
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
  
  /// Returns a paginated list of [PersonView] projections.
  ///
  /// @param parameters pagination and sorting options
  /// @return a [Page] of [PersonView] projections  
  @Transactional
  @Logged(value = "Found:", subject = "people")
  public Page<PersonView> findAll(@NotNull @Valid PersonQueryParameters parameters) {
    var pageRequest = PageRequest.ofPage(parameters.page(), parameters.size(), false);
    var results = repository.find(pageRequest);
    return results;
  }

  /// Returns a person by primary key, or empty if not found.
  ///
  /// @param id the person's primary key
  /// @param append unused, reserved for future association loading
  /// @return an [Optional] containing the person if found  
  @Transactional
  @Logged("Found:")
  public Optional<Person> findById(@NotNull @Positive Long id, String append) {
    var person = repository.findById(id);
    return person;
  }

  /// Updates an existing person by primary key.
  ///
  /// @param id the person's primary key
  /// @param command the fields to update
  /// @return the updated person
  /// @throws ResourceNotFoundException if no person exists with the given ID  
  @Transactional
  @Logged("Updated:")
  public Person update(Long id, UpdatePerson command) {
    var existingPerson = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No person found with id: " + id));
    mapper.merge(command, existingPerson);
    return repository.update(existingPerson);
  }

  /// Deletes a person by primary key.
  ///
  /// @param id the person's primary key
  /// @return the deleted person
  /// @throws ResourceNotFoundException if no person exists with the given ID  
  @Transactional
  @Logged("Deleted:")
  public Person delete(Long id) {
    var person = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No person found with id: " + id));
    repository.deleteById(id);
    return person;
  }
  
  private boolean isEqual(SavePerson command, Person person) {
    if (command == null || person == null) return false;
    return Objects.equals(command.tmdbId(), person.getTmdbId())
        && Objects.equals(command.name(), person.getName())
        && Objects.equals(command.birthDate(), person.getBirthDate())
        && Objects.equals(command.deathDate(), person.getDeathDate())
        && Objects.equals(command.gender(), person.getGender())
        && Objects.equals(command.profile() != null ? command.profile().name() : null, person.getProfile())
        && Objects.equals(command.homepage(), person.getHomepage())
        && Objects.equals(command.birthPlace(), person.getBirthPlace())
        && Objects.equals(command.biography(), person.getBiography());
  }
}
