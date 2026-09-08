package com.erdouglass.emdb.media.movie.adapter.out.logging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.movie.domain.event.MovieCreated;
import com.erdouglass.emdb.media.movie.domain.event.MovieUpdated;

@ApplicationScoped
class MovieLoggingAdapter {
  private static final Logger LOGGER = Logger.getLogger(MovieLoggingAdapter.class);

  void onCreated(@Observes(during = TransactionPhase.AFTER_SUCCESS) MovieCreated event) {
    LOGGER.infof("movie created: id=%s, tmdbId=%d, title=%s", 
        event.id().value(), event.tmdbId().value(), event.title().value());
  }
  
  void onUpdated(@Observes(during = TransactionPhase.AFTER_SUCCESS) MovieUpdated event) {
    LOGGER.infof("movie updated: id=%s, tmdbId=%d, title=%s", 
        event.id().value(), event.tmdbId().value(), event.title().value());
  }  
}
