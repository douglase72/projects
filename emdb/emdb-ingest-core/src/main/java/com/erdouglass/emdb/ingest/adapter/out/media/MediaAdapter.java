package com.erdouglass.emdb.ingest.adapter.out.media;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.out.Media;
import com.erdouglass.emdb.ingest.application.port.out.Movie;
import com.erdouglass.emdb.media.movie.SaveMovieUseCase;

@ApplicationScoped
class MediaAdapter implements Media {
  
  @Inject
  MovieMapper mapper;
  
  @Inject
  SaveMovieUseCase saveUseCase;

  @Override
  public void save(Movie movie) {
    saveUseCase.save(mapper.toSaveMovieCommand(movie));
  }
}
