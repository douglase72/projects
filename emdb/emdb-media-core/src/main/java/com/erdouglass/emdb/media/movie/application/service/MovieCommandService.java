package com.erdouglass.emdb.media.movie.application.service;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.common.PublicId;
import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveMovieUseCase;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveResult.Status;

@ApplicationScoped
class MovieCommandService implements SaveMovieUseCase {
  private static final Logger LOGGER = Logger.getLogger(MovieCommandService.class);

  @Override
  public SaveResult save(SaveMovieCommand command) {
    LOGGER.infof("command: %s", command);
    return SaveResult.of(PublicId.newId(), Status.CREATED);
  }
}
