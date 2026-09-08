package com.erdouglass.emdb.media.person.adapter.out.logging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.person.domain.event.PersonCreated;
import com.erdouglass.emdb.media.person.domain.event.PersonUpdated;

@ApplicationScoped
class PersonLoggingAdapter {
  private static final Logger LOGGER = Logger.getLogger(PersonLoggingAdapter.class);
  
  void onCreated(@Observes(during = TransactionPhase.AFTER_SUCCESS) PersonCreated event) {
    LOGGER.infof("person created: id=%s, tmdbId=%d, name=%s", 
        event.id().value(), event.tmdbId().value(), event.name().value());
  }
  
  void onUpdated(@Observes(during = TransactionPhase.AFTER_SUCCESS) PersonUpdated event) {
    LOGGER.infof("person updated: id=%s, tmdbId=%d, name=%s", 
        event.id().value(), event.tmdbId().value(), event.name().value());
  }  
}
