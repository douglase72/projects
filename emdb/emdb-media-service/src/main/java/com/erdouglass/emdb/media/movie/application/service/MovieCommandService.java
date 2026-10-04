package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.common.messaging.Correlation;
import com.erdouglass.emdb.media.messaging.MediaSavedMessage;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MediaPublisher;
import com.erdouglass.emdb.media.movie.domain.model.Movie;
import com.erdouglass.emdb.media.shared.application.SaveResult;
import com.erdouglass.emdb.media.shared.application.SaveResult.Status;
import com.erdouglass.emdb.shared.kernel.MediaType;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieCommandService.class);
  
  @Inject
  MediaPublisher media;
  
  @Override
  public SaveResult save(SaveMovieCommand command, Correlation correlation) {
    var movie = Movie.create(command.tmdbId(), command.details());
    LOGGER.info(movie);
    var event = MediaSavedMessage.of(movie.id(), movie.tmdbId(), MediaType.MOVIE);
    media.publish(event, correlation);
    return SaveResult.of(movie.id(), Status.CREATED);
  }
}
