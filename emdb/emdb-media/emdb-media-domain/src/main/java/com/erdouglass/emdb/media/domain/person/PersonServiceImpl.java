package com.erdouglass.emdb.media.domain.person;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.IngestMedia.IngestSource;
import com.erdouglass.emdb.ingest.IngestMedia.IngestType;
import com.erdouglass.emdb.ingest.IngestProducer;
import com.erdouglass.emdb.media.domain.PersonService;
import com.erdouglass.emdb.media.domain.internal.ImageService;
import com.erdouglass.emdb.media.domain.internal.PersonResolver;
import com.erdouglass.emdb.media.person.PersonCredit;
import com.erdouglass.emdb.media.person.PersonResponse;
import com.erdouglass.emdb.media.person.SavePerson;

@ApplicationScoped
class PersonServiceImpl implements PersonService, PersonResolver {
  private static final Logger LOGGER = Logger.getLogger(PersonServiceImpl.class);
  
  @Inject
  ImageService imageService;
  
  @Inject
  IngestProducer producer;
  
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

  @Override
  public Map<Integer, Person> findOrCreate(List<PersonCredit> credits) {
    if (credits.isEmpty()) {
      return Map.of();
    }    
    var distinct = credits.stream()
        .collect(Collectors.toMap(PersonCredit::tmdbId, Function.identity(), (a, _) -> a));
    
    var existing = repository.findByTmdbIdIn(List.copyOf(distinct.keySet())).stream()
        .collect(Collectors.toMap(Person::getTmdbId, Function.identity()));
    for (var credit : credits) {
      var person = existing.get(credit.tmdbId());
      if (person != null && !Objects.equals(person.getTmdbProfile(), credit.profile())) {
        var command = IngestMedia.of(person.getTmdbId(), IngestType.PERSON, IngestSource.MEDIA);
        producer.publish(command);
      }
    }
    
    var peopleToInsert = distinct.values().stream()
        .filter(c -> !existing.containsKey(c.tmdbId()))
        .map(PersonServiceImpl::toPerson)
        .toList();   
    for (var person : repository.insertAll(peopleToInsert)) {
      var tmdbId = person.getTmdbId();
      existing.put(tmdbId, person);
      var command = IngestMedia.of(tmdbId, IngestType.PERSON, IngestSource.MEDIA);
      producer.publish(command);
    }
    return existing;
  }
  
  private static Person toPerson(PersonCredit credit) {
    var person = new Person(credit.tmdbId());
    person.setName(credit.name());
    person.setGender(credit.gender());
    return person;
  }
}
