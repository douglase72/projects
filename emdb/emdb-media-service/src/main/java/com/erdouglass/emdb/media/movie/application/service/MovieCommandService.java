package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.media.movie.application.port.in.ReportMovieFailureCommand;
import com.erdouglass.emdb.media.movie.application.port.in.ReportMovieFailureUseCase;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MovieCommandRepository;
import com.erdouglass.emdb.media.movie.application.port.out.MovieOutbox;
import com.erdouglass.emdb.media.movie.domain.event.MovieEvent;
import com.erdouglass.emdb.media.movie.domain.event.MovieFailed;
import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.shared.application.SaveResult;
import com.erdouglass.emdb.media.shared.application.SaveResult.Status;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase, ReportMovieFailureUseCase {  
  
  @Inject
  Event<MovieEvent> emitter;
  
  @Inject
  MovieCommandRepository movies;
  
  @Inject
  MovieOutbox outbox;

  @Override
  @Transactional
  public SaveResult save(SaveMovieCommand command) {
    return movies.findByTmdbId(command.tmdbId())
        .map(existing -> update(existing, command))
        .orElseGet(() -> insert(command));
  }
  
  @Override
  @Transactional(TxType.REQUIRES_NEW)
  public void report(ReportMovieFailureCommand command) {
    var event = MovieFailed.builder()
        .id(MessageId.newId())
        .correlationId(command.correlationId())
        .tmdbId(command.tmdbId())
        .reason(command.reason())
        .build();
    publish(event);
  }
  
  private SaveResult insert(SaveMovieCommand command) {
    var movie = Movie.create(command.correlationId(), command.tmdbId(), command.details());
    movies.insert(movie);
    movie.pullEvents().forEach(this::publish);
    return SaveResult.of(movie.id(), Status.CREATED);
  }
  
  private void publish(MovieEvent event) {
    outbox.save(event);
    emitter.fire(event);
  }
  
  private SaveResult update(Movie existing, SaveMovieCommand command) {
    existing.update(command.correlationId(), command.details());
    movies.update(existing);
    existing.pullEvents().forEach(this::publish);
    return SaveResult.of(existing.id(), Status.UPDATED);
  }
}
