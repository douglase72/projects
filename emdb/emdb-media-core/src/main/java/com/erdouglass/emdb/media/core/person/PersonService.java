package com.erdouglass.emdb.media.core.person;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.emdb.media.core.ImageService;
import com.erdouglass.emdb.media.core.Log;
import com.erdouglass.emdb.media.person.PersonCommandService;
import com.erdouglass.emdb.media.person.PersonDto;
import com.erdouglass.emdb.media.person.SavePerson;
import com.erdouglass.emdb.media.person.UpdatePerson;

@ApplicationScoped
class PersonService implements PersonCommandService {
  
  @Inject
  ImageService imageService;
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonRepository personRepository;

  @Override
  @Log("Saved:")
  @Transactional
  public PersonDto save(SavePerson command) {
    Person person;
    var existing = personRepository.findByTmdbId(command.tmdbId()).orElse(null); 
    if (existing == null) {
      imageService.save(command.profile());
      person = personRepository.insert(mapper.toPerson(command));
    } else {
      var profile = imageService.update(existing.getProfile(), command.profile());
      var cmd = SavePerson.builder(command)
          .profile(profile.image())
          .build();
      mapper.merge(cmd, existing);
      person = personRepository.update(existing);
      profile.toDelete().ifPresent(imageService::delete);
    }
    return mapper.toPersonDto(person);
  }

  @Override
  @Transactional
  public PersonDto update(UpdatePerson command) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Transactional
  public void delete(Long id) {
    throw new UnsupportedOperationException();
  }
}
