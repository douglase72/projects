package com.erdouglass.emdb.media.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.comand.SavePerson;
import com.erdouglass.emdb.common.comand.UpdatePerson;
import com.erdouglass.emdb.common.event.IngestStatusChanged;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestSource;
import com.erdouglass.emdb.common.event.IngestStatusChanged.IngestStatus;
import com.erdouglass.emdb.common.query.PersonDto;
import com.erdouglass.emdb.media.dto.PersonStatus;
import com.erdouglass.emdb.media.dto.PersonStatus.Status;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.entity.Person_;
import com.erdouglass.emdb.media.mapper.PersonMapper;
import com.erdouglass.emdb.media.repository.CreditRepository;
import com.erdouglass.emdb.media.repository.PersonRepository;
import com.erdouglass.emdb.scraper.service.TmdbPersonScraper;
import com.erdouglass.webservices.ResourceNotFoundException;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;

@ApplicationScoped
public class PersonService extends MediaService {
  private static final Logger LOGGER = Logger.getLogger(PersonService.class);
  private static final String ROUTE_KEY = "person.invalid";
  
  @Inject
  CreditRepository creditRepository;  
  
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
  @ActivateRequestContext
  public Duration ingest(@NotNull @Positive Integer tmdbId, @NotNull UUID jobId) {
    var start = Instant.now();
    var existingPerson = repository.findByTmdbId(tmdbId);
    var command = existingPerson
        .map(mapper::toSavePerson)
        .orElseGet(() -> SavePerson.builder().tmdbId(tmdbId).build());
    var saveCommand = scraper.scrape(command, jobId);
    
    try {
      validate(saveCommand);
      var person = QuarkusTransaction.requiringNew().call(() -> savePerson(saveCommand));
      LOGGER.infof("Saved: %s", person);
      existingPerson.ifPresent(p -> {
        if (!Objects.equals(p.tmdbProfile().orElse(null), person.tmdbProfile().orElse(null))) {
          p.profile().ifPresent(imageService::delete);
        }
      });
      var et = Duration.between(start, Instant.now());
      var msg = String.format("Ingest Job for TMDB %s %d completed in %d ms", 
          MediaType.PERSON, person.tmdbId(), et.toMillis());
      LOGGER.info(msg);      
      statusService.send(IngestStatusChanged.builder()
          .id(jobId)
          .status(IngestStatus.COMPLETED)
          .tmdbId(tmdbId)
          .source(IngestSource.MEDIA)
          .type(MediaType.PERSON)
          .name(person.name())
          .emdbId(person.id())
          .message(msg)
          .build());
      return et;
    } catch (Exception e) {
      dlqEmitter.send(Message.of(saveCommand)
          .addMetadata(OutgoingRabbitMQMetadata.builder()
          .withRoutingKey(ROUTE_KEY)
          .build()));
      throw new RuntimeException(e);      
    }
  }
  
  @Transactional
  public PersonDto save(SavePerson command) {
    long start = System.nanoTime();
    var person = savePerson(command);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Saved %s in %d ms", person, et);
    return mapper.toPersonDto(person);
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
        results.add(PersonStatus.of(existingPerson, Status.UNCHANGED));
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
  public PersonDto findById(@NotNull @Positive Long id, String append) {
    long start = System.nanoTime();
    var person = findPersonById(id, append);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Found %s in %d ms", person, et);
    return mapper.toPersonDto(person);
  }
  
  @Transactional
  public List<Person> findByIdIn(List<Long> ids) {
    return repository.findByIdIn(ids);
  }
  
  @Transactional
  public PersonDto update(Long id, UpdatePerson command) {
    long start = System.nanoTime();
    var existingPerson = findPersonById(id, "credits");
    var newPerson = mapper.toPerson(command);
    newPerson.id(existingPerson.id());
    newPerson.tmdbId(existingPerson.tmdbId());
    var updatedPerson = repository.update(newPerson);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Updated %s in %d ms", updatedPerson, et);
    return mapper.toPersonDto(updatedPerson);
  }
  
  @Transactional
  public void deleteById(Long id) {
    long start = System.nanoTime();
    var person = findPersonById(id, null);
    person.profile().ifPresent(imageService::delete);
    repository.deleteById(id);
    var et = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    LOGGER.infof("Deleted %s in %d ms", person, et);
  }
  
  private Person savePerson(SavePerson command) {
    var person = mapper.toPerson(command);
    repository.findByTmdbId(person.tmdbId()).ifPresent(p -> person.id(p.id()));
    var savedPerson = repository.save(person);
    return savedPerson;
  }
  
  private Person findPersonById(Long id, String append) {
    var person = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No person found with id: " + id));
    person.credits(List.of());
    if (append != null && append.contains(Person_.CREDITS)) {
      person.credits(creditRepository.findByPersonId(id));
    }
    return person;    
  }
  
}
