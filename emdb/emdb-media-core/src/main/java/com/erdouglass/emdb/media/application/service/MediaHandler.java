package com.erdouglass.emdb.media.application.service;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.MediaFacade;
import com.erdouglass.emdb.media.SaveMovie;
import com.erdouglass.emdb.media.SavePerson;
import com.erdouglass.emdb.media.SaveResult;
import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.SaveSeries;

@ApplicationScoped
class MediaHandler implements MediaFacade {
  private static final Logger LOGGER = Logger.getLogger(MediaHandler.class);

  @Override
  public SaveResult saveMovie(SaveMovie command) {
    LOGGER.infof("Saved: %s", command.title());
    return new SaveResult(1L, Status.CREATED);
  }

  @Override
  public SaveResult savePerson(SavePerson command) {
    throw new UnsupportedOperationException();
  }

  @Override
  public SaveResult saveSeries(SaveSeries command) {
    throw new UnsupportedOperationException();
  }
}
