package com.erdouglass.emdb.media.application.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.PersonCredit;
import com.erdouglass.emdb.media.SavePerson;
import com.erdouglass.emdb.media.SavePersonUseCase;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.application.port.inbound.person.DeletePersonUseCase;
import com.erdouglass.emdb.media.application.port.inbound.person.PersonView;
import com.erdouglass.emdb.media.application.port.inbound.person.QueryPersonUseCase;
import com.erdouglass.emdb.media.application.port.inbound.person.UpdatePerson;
import com.erdouglass.emdb.media.application.port.inbound.person.UpdatePersonUseCase;
import com.erdouglass.emdb.media.domain.person.Person;
import com.erdouglass.emdb.media.domain.person.PersonRepository;

@ApplicationScoped
class PersonService implements SavePersonUseCase, UpdatePersonUseCase, DeletePersonUseCase,
    QueryPersonUseCase, PersonResolver {
  private static final Logger LOGGER = Logger.getLogger(PersonService.class);
  
  @Inject
  ImageService imageService;
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonRepository repository;
  
  @Override
  @Transactional
  public SaveResult save(SavePerson command) {
    SaveResult result;
    Person person;
    var existing = repository.findByExternalId(command.externalId()).orElse(null); 
    if (existing == null) {
      imageService.save(command.profile());
      person = repository.insert(mapper.toPerson(command));
      result = new SaveResult(person.getId(), Status.CREATED);
    } else {
      var profile = imageService.update(existing.getProfile(), command.profile());
      var cmd = SavePerson.builder(command)
          .profile(profile.image())
          .build();
      mapper.merge(cmd, existing);
      person = repository.update(existing);
      profile.toDelete().ifPresent(imageService::delete);
      result = new SaveResult(person.getId(), Status.UPDATED);
    }
    LOGGER.infof("Saved: %s", person);
    return result;
  }

  @Override
  public PersonView findById(Long id) {
    return repository.findById(id)
        .map(mapper::toPersonView)
        .orElseThrow(() -> new ResourceNotFoundException("No person found with id: " + id));  
  }
  
  @Override
  public Map<Long, Long> findOrCreate(List<PersonCredit> credits) {
    var distinct = credits.stream()
        .collect(Collectors.toMap(PersonCredit::externalId, Function.identity(), (a, _) -> a));
    var existing = repository.findByExternalIdIn(List.copyOf(distinct.keySet())).stream()
        .collect(Collectors.toMap(Person::getExternalId, Person::getId));   
    var peopleToInsert = distinct.values().stream()
        .filter(c -> !existing.containsKey(c.externalId()))
        .map(PersonService::toPerson)
        .toList();
    for (var person : repository.insertAll(peopleToInsert)) {
      var externalId = person.getExternalId();
      existing.put(externalId, person.getId());
    }
    return existing;     
  }
  
  @Override
  public PersonView update(Long id, UpdatePerson command) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteById(Long id) {
    throw new UnsupportedOperationException();
  }
  
  private static Person toPerson(PersonCredit credit) {
    var person = new Person(credit.externalId());
    person.setName(credit.name());
    person.setGender(credit.gender());
    return person;
  }
}
