package com.erdouglass.emdb.ingest.scraper.person;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.scraper.Scraper;
import com.erdouglass.emdb.ingest.scraper.image.ImageScraper;
import com.erdouglass.emdb.media.command.SavePerson;

@ApplicationScoped
class PersonScraper extends Scraper<SavePerson> {

  @Inject
  @RestClient
  PersonClient client;

  @Inject
  @Channel("save-person-out")
  Emitter<SavePerson> emitter;

  @Inject
  ImageScraper imageScraper;
  
  @Inject
  PersonMapper mapper;
  
  @Override
  protected Emitter<SavePerson> emitter() {
    return emitter;
  }
  
  @Override
  protected SavePerson extract(final int tmdbId) {
    var person = client.findById(tmdbId);
    var profile = imageScraper.extract(person.profile_path());
    var command = mapper.toSavePerson(person, profile);
    return command;
  }
}
