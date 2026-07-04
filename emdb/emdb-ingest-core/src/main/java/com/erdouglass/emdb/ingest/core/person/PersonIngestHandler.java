package com.erdouglass.emdb.ingest.core.person;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Message;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.core.IngestHandler;
import com.erdouglass.emdb.ingest.logging.Log;
import com.erdouglass.emdb.media.person.PersonCommandService;
import com.erdouglass.emdb.media.person.PersonDto;
import com.erdouglass.emdb.media.person.SavePerson;

@ApplicationScoped
public class PersonIngestHandler extends IngestHandler<SavePerson, PersonDto> {
  
  @ConfigProperty(name = "emdb.person.data")
  String path;
  
  @Inject
  PersonScraper scraper;
  
  @Inject
  PersonCommandService service;

  @Log
  @Override
  public PersonDto ingest(Message<IngestMedia> message) throws IOException {
    var command = scraper.scrape(message);
    
    try {
      return service.save(command);
    } catch (ConstraintViolationException e) {
      var cmd = SavePerson.builder(command)
          .profile(saveImage(command.profile()))
          .build();      
      saveMessage(message, cmd);
      throw e;
    }    
  }

  @Override
  protected String mediaPath() {
    return path;
  }
}
