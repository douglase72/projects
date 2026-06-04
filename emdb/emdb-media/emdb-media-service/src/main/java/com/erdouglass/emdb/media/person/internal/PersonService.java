package com.erdouglass.emdb.media.person.internal;

import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.command.SavePerson;
import com.erdouglass.emdb.media.internal.ImageService;
import com.erdouglass.emdb.media.internal.Log;
import com.erdouglass.emdb.media.internal.PersonData;
import com.erdouglass.emdb.media.internal.PersonResolver;

@ApplicationScoped
class PersonService implements PersonResolver {
  
  @Inject
  ImageService imageService;
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonRepository repository;
  
  @Log
  @Transactional
  public Person save(final SavePerson command) {
    Person person;
    var existing = repository.findByTmdbId(command.tmdbId()).orElse(null); 
    if (existing == null) {
      var profile = imageService.save(command.profile());
      person = repository.insert(mapper.toPerson(command, profile));
    } else {
      var profile = imageService.update(existing.getTmdbProfile(), existing.getProfile(), command.profile());
      var cmd = SavePerson.builder(command)
          .profile(profile.image())
          .build();
      mapper.merge(cmd, existing);
      person = repository.update(existing);
      profile.toDelete().ifPresent(imageService::delete);
    }    
    return person;
  }

  @Override
  public Map<Integer, PersonData> findOrCreate() {
    throw new UnsupportedOperationException();
  }
}
