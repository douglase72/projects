package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.media.messaging.MediaEvent;
import com.erdouglass.emdb.media.messaging.MediaEvent.EventType;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.movie.application.port.out.MediaEventPublisher;
import com.erdouglass.emdb.media.shared.application.SaveResult;
import com.erdouglass.emdb.media.shared.application.SaveResult.Status;
import com.erdouglass.emdb.shared.kernel.MediaType;
import com.erdouglass.emdb.shared.kernel.PublicId;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieCommandService.class);
  
  @Inject
  MediaEventPublisher events;

  @Override
  public SaveResult save(SaveMovieCommand command) {
    LOGGER.infof("command: %s", command);
    var event = MediaEvent.builder()
        .messageId(MessageId.newId())
        .correlationId(command.correlationId())
        .mediaId(PublicId.newId())
        .tmdbId(command.tmdbId())
        .mediaType(MediaType.MOVIE)
        .eventType(EventType.SAVED)
        .build();
    events.publish(event);
    return SaveResult.of(event.mediaId(), Status.CREATED);
  }
}
