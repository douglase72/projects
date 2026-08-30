package com.erdouglass.emdb.media.movie.adapter.in.ingest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.api.LoadMovieCommand;
import com.erdouglass.emdb.media.api.MediaFacade;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;

@ApplicationScoped
class IngestAdapter implements MediaFacade {
  
  @Inject
  SaveMovieUseCase saveUseCase;

  @Override
  public void load(LoadMovieCommand command) {
    var cmd = CommandMapper.toSaveMovieCommand(command);
    saveUseCase.save(cmd);
  }
}
