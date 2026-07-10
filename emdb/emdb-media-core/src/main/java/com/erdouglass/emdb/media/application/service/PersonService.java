package com.erdouglass.emdb.media.application.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.PersonCredit;
import com.erdouglass.emdb.media.application.port.inbound.PersonCommandService;
import com.erdouglass.emdb.media.application.port.inbound.PersonQueryService;
import com.erdouglass.emdb.media.application.port.inbound.PersonView;
import com.erdouglass.emdb.media.application.port.inbound.UpdatePerson;
import com.erdouglass.emdb.media.domain.person.Person;
import com.erdouglass.emdb.media.domain.person.PersonRepository;

@ApplicationScoped
class PersonService implements PersonCommandService, PersonQueryService, PersonResolver {
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonRepository repository;

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
  public PersonView update(UpdatePerson command) {
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
