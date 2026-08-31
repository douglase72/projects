package com.erdouglass.emdb.media.movie.adapter.in.ingest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.api.LoadMovieCommand;
import com.erdouglass.emdb.media.api.LoadPersonCommand;
import com.erdouglass.emdb.media.api.MediaFacade;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieUseCase;
import com.erdouglass.emdb.media.person.application.port.in.SavePersonUseCase;

@ApplicationScoped
class IngestAdapter implements MediaFacade {
  
  @Inject
  SaveMovieUseCase saveMovieUseCase;
  
  @Inject
  SavePersonUseCase savePersonUseCase;

  @Override
  public void load(LoadMovieCommand command) {
    saveMovieUseCase.save(MovieCommandMapper.toSaveMovieCommand(command));
  }

  @Override
  public void load(LoadPersonCommand command) {
    savePersonUseCase.save(PersonCommandMapper.toSavePersonCommand(command));    
  }
}
