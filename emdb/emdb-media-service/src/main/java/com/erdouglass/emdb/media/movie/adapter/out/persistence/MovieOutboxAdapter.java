package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.messaging.MediaEvent.EventType;
import com.erdouglass.emdb.media.movie.application.port.out.MovieOutbox;
import com.erdouglass.emdb.media.movie.domain.event.MovieCreated;
import com.erdouglass.emdb.media.movie.domain.event.MovieEvent;
import com.erdouglass.emdb.media.movie.domain.event.MovieFailed;
import com.erdouglass.emdb.media.movie.domain.event.MovieUpdated;

@ApplicationScoped
class MovieOutboxAdapter implements MovieOutbox {
  
  @Inject
  MovieOutboxRepository outbox;

  @Override
  public void save(MovieEvent event) {
    var entity = new MovieOutboxEntity();
    entity.setId(event.id().value());
    entity.setCorrelationId(event.correlationId().value());
    entity.setTmdbId(event.tmdbId().value());
    switch (event) {
      case MovieCreated e -> { entity.setMediaId(e.mediaId().value()); entity.setType(EventType.CREATED); }
      case MovieUpdated e -> { entity.setMediaId(e.mediaId().value()); entity.setType(EventType.UPDATED); }
      case MovieFailed  e -> { entity.setReason(e.reason()); entity.setType(EventType.FAILED); }
    }
    outbox.insert(entity);    
  }
}
