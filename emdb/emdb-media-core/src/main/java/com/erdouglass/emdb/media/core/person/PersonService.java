package com.erdouglass.emdb.media.core.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.IngestMedia;
import com.erdouglass.emdb.media.IngestMedia.Source;
import com.erdouglass.emdb.media.IngestMedia.Type;
import com.erdouglass.emdb.media.IngestService;
import com.erdouglass.emdb.media.core.ImageService;
import com.erdouglass.emdb.media.core.credit.Credit;
import com.erdouglass.emdb.media.core.logging.Log;
import com.erdouglass.emdb.media.core.movie.MovieCreditRepository;
import com.erdouglass.emdb.media.person.PersonCommandService;
import com.erdouglass.emdb.media.person.PersonCredit;
import com.erdouglass.emdb.media.person.PersonDto;
import com.erdouglass.emdb.media.person.PersonDto.PersonCredits;
import com.erdouglass.emdb.media.person.PersonQueryService;
import com.erdouglass.emdb.media.person.SavePerson;
import com.erdouglass.emdb.media.person.UpdatePerson;

@ApplicationScoped
class PersonService implements PersonCommandService, PersonQueryService, PersonResolver {
  
  @Inject
  ImageService imageService;
  
  @Inject
  MovieCreditRepository movieCreditRepository;
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonRepository repository;
  
  @Inject
  IngestService ingestService;

  @Override
  @Log("Saved:")
  @Transactional
  public PersonDto save(SavePerson command) {
    Person person;
    var existing = repository.findByTmdbId(command.tmdbId()).orElse(null); 
    if (existing == null) {
      imageService.save(command.profile());
      person = repository.insert(mapper.toPerson(command));
    } else {
      var profile = imageService.update(existing.getProfile(), command.profile());
      var cmd = SavePerson.builder(command)
          .profile(profile.image())
          .build();
      mapper.merge(cmd, existing);
      person = repository.update(existing);
      profile.toDelete().ifPresent(imageService::delete);
    }
    return mapper.toPersonDto(person);
  }
  
  @Override
  @Log("Found:")
  @Transactional
  public PersonDto findById(Long id) {
    return repository.findById(id)
        .map(mapper::toPersonDto)
        .orElseThrow(() -> new ResourceNotFoundException("No person found with id: " + id));
  }
  
  @Override
  @Transactional
  public PersonCredits findCreditsByPersonId(Long id) {
    List<Credit> credits = new ArrayList<>();
    credits.addAll(movieCreditRepository.findByPersonId(id));
    return mapper.toCredits(credits);
  }
  
  @Override
  @Transactional
  public Map<Integer, Person> findOrCreate(List<PersonCredit> credits) {
    var distinct = credits.stream()
        .collect(Collectors.toMap(PersonCredit::tmdbId, Function.identity(), (a, _) -> a));
    var existing = repository.findByTmdbIdIn(List.copyOf(distinct.keySet())).stream()
        .collect(Collectors.toMap(Person::getTmdbId, Function.identity()));
    var peopleToInsert = distinct.values().stream()
        .filter(c -> !existing.containsKey(c.tmdbId()))
        .map(PersonService::toPerson)
        .toList();  
    for (var person : repository.insertAll(peopleToInsert)) {
      var tmdbId = person.getTmdbId();
      existing.put(tmdbId, person);
      ingestService.publish(IngestMedia.of(tmdbId, Type.PERSON, Source.MEDIA));
    }
    return existing;      
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
  
  private static Person toPerson(final PersonCredit credit) {
    var person = new Person(credit.tmdbId());
    person.setName(credit.name());
    person.setGender(credit.gender());
    return person;
  }
}
