package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.application.port.in.PersonCommandService;
import com.erdouglass.emdb.media.application.port.in.PersonQueryService;
import com.erdouglass.emdb.media.application.port.in.PersonView;
import com.erdouglass.emdb.media.application.port.in.UpdatePerson;
import com.erdouglass.emdb.media.domain.person.PersonRepository;

@ApplicationScoped
class PersonService implements PersonCommandService, PersonQueryService {
  
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
  public PersonView update(UpdatePerson command) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteById(Long id) {
    throw new UnsupportedOperationException();
  }
}
