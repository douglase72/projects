package com.erdouglass.emdb.ingest.core.person;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.IngestMedia;
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
  public SavePerson scrape(Message<IngestMedia> message) {
    var tmdbId = message.getPayload().tmdbId();
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
