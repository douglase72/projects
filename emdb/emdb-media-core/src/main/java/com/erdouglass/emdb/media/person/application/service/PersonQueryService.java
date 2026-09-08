package com.erdouglass.emdb.media.person.application.service;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.person.application.port.in.FindPersonUseCase;
import com.erdouglass.emdb.media.person.application.port.out.PersonQueryRepository;
import com.erdouglass.emdb.media.person.application.port.out.PersonView;

@ApplicationScoped
class PersonQueryService implements FindPersonUseCase {
  
  @Inject
  PersonQueryRepository people;

  @Override
  public Optional<PersonView> findById(PublicId id) {
    return people.findById(id);
  }
}
