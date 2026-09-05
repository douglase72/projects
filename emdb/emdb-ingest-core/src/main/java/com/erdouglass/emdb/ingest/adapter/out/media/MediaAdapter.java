package com.erdouglass.emdb.ingest.adapter.out.media;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.out.PersonRepository;
import com.erdouglass.emdb.ingest.application.port.out.Person;
import com.erdouglass.emdb.media.api.LoadPersonUseCase;

/// Anti-corruption layer between the Ingest bounded context and the Media 
/// bounded context lets each context evolve their domain models independently.
@ApplicationScoped
class MediaAdapter implements PersonRepository {
  
  @Inject
  LoadPersonUseCase loadPersonUseCase;
  
  @Inject
  CommandMapper mapper;

  @Override
  public void save(Person person) {
    loadPersonUseCase.load(mapper.toLoadPersonCommand(person));
  }
}
