package com.erdouglass.emdb.ingest.scraper.person;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.erdouglass.emdb.ingest.scraper.Scraper;
import com.erdouglass.emdb.media.api.command.SavePerson;

/// [Scraper] implementation that fetches people from TMDB and emits the
/// resulting [SavePerson] commands on the `save-person-out` channel.
@ApplicationScoped
class PersonScraper extends Scraper<SavePerson> {

@Inject
@RestClient
PersonClient client;

@Inject
@Channel("save-person-out") 
Emitter<SavePerson> emitter;

@Override
protected SavePerson extract(int tmdbId) {
  var person = client.findById(tmdbId);
  var command = new SavePerson(person.id(), person.name());
  return command;
}

@Override
protected Emitter<SavePerson> getEmitter() {
  return emitter;
}
}