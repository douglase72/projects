package com.erdouglass.emdb.ingest.core.person;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.core.Scraper;
import com.erdouglass.emdb.ingest.logging.Log;
import com.erdouglass.emdb.media.person.SavePerson;

@ApplicationScoped
class PersonScraper extends Scraper<Person> {
  
  @Inject
  @RestClient
  TmdbPersonClient client;
  
  @Inject
  TmdbPersonMapper mapper;
  
  @Inject
  PersonRepository repository;

  @Log
  @Transactional
  public SavePerson scrape(@NotNull @Positive Integer tmdbId) {
    var tmdbPerson = client.findById(tmdbId);
    var existing = repository.findById(tmdbId).orElse(null);
    var profile = resolveImage(existing, tmdbPerson.profile_path(),
        Person::getTmdbProfile, Person::getEmdbProfile);
    var person = existing != null ? existing : new Person(tmdbId);
    person.setEmdbProfile(nameOf(profile));
    person.setTmdbProfile(tmdbPerson.profile_path());
    repository.save(person); 
    return mapper.toSavePerson(tmdbPerson, profile);
  }
}
