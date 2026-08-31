package com.erdouglass.emdb.media.api;

public interface MediaFacade {
  
  void load(LoadMovieCommand command);
  
  void load(LoadPersonCommand command);
}
