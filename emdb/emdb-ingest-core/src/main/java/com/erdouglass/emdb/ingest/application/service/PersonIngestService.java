package com.erdouglass.emdb.ingest.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.common.util.DateTimeFactory;
import com.erdouglass.emdb.ingest.application.port.out.PersonRepository;
import com.erdouglass.emdb.ingest.application.port.out.Person;
import com.erdouglass.emdb.media.api.TmdbId;

@ApplicationScoped
class PersonIngestService {
  private static final Logger LOGGER = Logger.getLogger(PersonIngestService.class);
  
  @Inject
  PersonRepository repository;
  
  public void ingest() {
    var person = Person.builder()
        .tmdbId(TmdbId.of(3))
        .name("Harrison Ford")
        .birthDate(DateTimeFactory.from("1942-07-13"))
        .gender("male")
        .biography("Test biography")
        .build();
    repository.save(person);
    LOGGER.debugf("Loaded: %s", person); 
  }
}
