package com.erdouglass.emdb.ingest.core.person;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.IngestMedia;
import com.erdouglass.emdb.ingest.IngestMedia.Source;
import com.erdouglass.emdb.ingest.IngestService;
import com.erdouglass.emdb.media.MediaType;
import com.erdouglass.emdb.media.person.PersonCreditCreated;

@ApplicationScoped
class PersonObserver {
  
  @Inject
  IngestService service;
  
  public void onEvent(@Observes PersonCreditCreated event) {
    service.publish(IngestMedia.of(event.tmdbId(), MediaType.PERSON, Source.MEDIA));
  }
}
