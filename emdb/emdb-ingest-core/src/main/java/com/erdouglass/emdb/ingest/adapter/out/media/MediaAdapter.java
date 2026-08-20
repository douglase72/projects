package com.erdouglass.emdb.ingest.adapter.out.media;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.dto.Movie;
import com.erdouglass.emdb.ingest.application.dto.Person;
import com.erdouglass.emdb.ingest.application.port.out.Media;
import com.erdouglass.emdb.media.movie.SaveMovieUseCase;
import com.erdouglass.emdb.media.person.SavePersonUseCase;

@ApplicationScoped
class MediaAdapter implements Media {
  
  @Inject
  MediaMapper mapper;
  
  @Inject
  SaveMovieUseCase saveMovieUseCase;
  
  @Inject
  SavePersonUseCase savePersonUseCase;

  @Override
  public void save(Movie movie) {
    saveMovieUseCase.save(mapper.toSaveMovieCommand(movie));
  }

  @Override
  public void save(Person person) {
    savePersonUseCase.save(mapper.toSavePersonCommand(person));    
  }
}
