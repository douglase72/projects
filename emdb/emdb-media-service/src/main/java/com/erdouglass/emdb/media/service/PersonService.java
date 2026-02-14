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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.media.dto.PersonStatus;
import com.erdouglass.emdb.media.dto.PersonStatus.Status;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.repository.PersonRepository;
import com.erdouglass.emdb.scraper.service.TmdbPersonScraper;
import com.erdouglass.webservices.ResourceNotFoundException;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class PersonService extends MediaService {
  private static final Logger LOGGER = Logger.getLogger(PersonService.class);
  private static final String ROUTE_KEY = "person.invalid";
  
  @Inject
  @Channel("person-dlq-out")
  Emitter<SavePerson> dlqEmitter;
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  TmdbPersonScraper scraper;
  
  @Inject
  PersonRepository repository;
  
  @Override
  public void ingest(@NotNull @Positive Integer tmdbId, String jobId) {
    var existingPerson = findByTmdbId(tmdbId);
    var command = existingPerson
        .map(mapper::toSavePerson)
        .orElseGet(() -> SavePerson.builder().tmdbId(tmdbId).build());
    var saveCommand = scraper.scrape(command, jobId);
    
    try {
      validate(saveCommand);
      var person = save(saveCommand);
      LOGGER.infof("Saved: %s", person);
      existingPerson.ifPresent(p -> {
        if (!Objects.equals(p.tmdbProfile().orElse(null), person.tmdbProfile().orElse(null))) {
          p.profile().ifPresent(imageService::delete);
        }
      }); 
    } catch (Exception e) {
      dlqEmitter.send(Message.of(saveCommand)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(ROUTE_KEY)
          .build()));
      throw new RuntimeException(e);      
    }
  }
  
  @Transactional
  public Person save(SavePerson command) {
    var person = mapper.toPerson(command);
    repository.findByTmdbId(person.tmdbId()).ifPresent(p -> person.id(p.id()));
    var savedPerson = repository.save(person);
    return savedPerson;  
  }
  
  @Transactional
  public List<PersonStatus> saveAll(List<SavePerson> commands) {
    var people = commands.stream().map(mapper::toPerson).toList();
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
  public List<Person> findByIdIn(List<Long> ids) {
    return repository.findByIdIn(ids);
  }
  
  @Transactional
  Optional<Person> findByTmdbId(@NotNull @Positive Integer id) {
    return repository.findByTmdbId(id);
  }
  
  @Transactional
  public Person update(Long id, UpdatePerson command) {
    var existingPerson = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Person not found with Id: " + id));
    var person = mapper.toPerson(command);
    person.id(existingPerson.id());
    person.tmdbId(existingPerson.tmdbId());
    var updatedPerson = repository.update(person);
    return updatedPerson;
  }
  
  @Transactional
  public void deleteById(Long id) {
    var movie = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No person found with id: " + id));
    repository.deleteById(id);
    LOGGER.infof("Deleted: %s", movie);
  }
  
}
