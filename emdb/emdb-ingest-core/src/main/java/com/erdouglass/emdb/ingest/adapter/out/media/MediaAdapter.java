package com.erdouglass.emdb.ingest.adapter.out.media;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.out.Media;
import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.media.api.MediaFacade;

/// Anti-corruption layer between the Ingest bounded context and the Media 
/// bounded context lets each context evolve their 'Movie' domain models 
/// independently.
@ApplicationScoped
class MediaAdapter implements Media {
  
  @Inject
  MediaFacade facade;
  
  @Inject
  CommandMapper mapper;

  @Override
  public void save(Movie movie) {
    var command = mapper.toSaveMovieCommand(movie);
    facade.load(command);
  }
}
