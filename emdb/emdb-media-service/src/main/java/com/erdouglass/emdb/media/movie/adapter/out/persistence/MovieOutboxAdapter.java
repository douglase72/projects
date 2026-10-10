package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.media.movie.application.port.out.MovieOutbox;
import com.erdouglass.emdb.media.movie.domain.event.MovieEvent;

@ApplicationScoped
class MovieOutboxAdapter implements MovieOutbox {
  
  @Inject
  MovieOutboxRepository outbox;

  @Override
  public boolean hasProcessed(MessageId id) {
    return outbox.findById(id.value()).isPresent();
  }

  @Override
  public void markProcessed(MovieEvent event) {
    var entity = new MovieOutboxEntity();
    entity.setId(event.id().value());
    entity.setCorrelationId(event.correlationId().value());
    entity.setMediaId(event.mediaId().value());
    entity.setTmdbId(event.tmdbId().value());
    outbox.insert(entity);    
  }
}
