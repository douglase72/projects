package com.erdouglass.emdb.media.domain.person;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.api.command.SavePerson;
import com.erdouglass.emdb.media.api.query.PersonResponse;
import com.erdouglass.emdb.media.domain.ImageService;
import com.erdouglass.emdb.media.domain.PersonService;

@ApplicationScoped
public class PersonServiceImpl implements PersonService {
  private static final Logger LOGGER = Logger.getLogger(PersonServiceImpl.class);
  
  @Inject
  ImageService imageService;
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonRepository repository;

  @Override
  @Transactional
  public PersonResponse save(final SavePerson command) {
    Person savedPerson;
    var existing = repository.findByTmdbId(command.tmdbId()).orElse(null); 
    if (existing == null) {
      var profile = imageService.save(command.profile());
      savedPerson = repository.insert(mapper.toPerson(command, profile));
    } else {
      var profile = imageService.update(existing.getTmdbProfile(), existing.getProfile(), command.profile());
      var cmd = SavePerson.builder(command)
          .profile(profile.image())
          .build();
      mapper.merge(cmd, existing);
      savedPerson = repository.update(existing);
      profile.toDelete().ifPresent(imageService::delete);
    }
    LOGGER.infof("Saved: %s", savedPerson);
    return mapper.toPersonResponse(savedPerson);
  }
}
