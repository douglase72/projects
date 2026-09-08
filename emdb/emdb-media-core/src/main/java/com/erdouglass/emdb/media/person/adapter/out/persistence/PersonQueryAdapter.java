package com.erdouglass.emdb.media.person.adapter.out.persistence;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.person.application.port.out.PersonQueryRepository;
import com.erdouglass.emdb.media.person.application.port.out.PersonView;

@ApplicationScoped
class PersonQueryAdapter implements PersonQueryRepository {
  
  @Inject
  JakartaDataPersonQueryRepository repository;

  @Override
  public Optional<PersonView> findById(PublicId id) {
    return repository.findById(id.value());
  }
}
