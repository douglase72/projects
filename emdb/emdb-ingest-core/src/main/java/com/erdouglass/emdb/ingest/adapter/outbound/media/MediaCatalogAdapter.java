package com.erdouglass.emdb.ingest.adapter.outbound.media;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.outbound.Media;
import com.erdouglass.emdb.ingest.application.port.outbound.MediaCatalog;
import com.erdouglass.emdb.ingest.application.port.outbound.Movie;
import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveMovieUseCase;

@ApplicationScoped
class MediaCatalogAdapter implements MediaCatalog {
  
  @Inject
  SaveMovieUseCase saveMovieUseCase;

  @Override
  public void load(Media media) {
    switch (media) {
      case Movie movie -> saveMovieUseCase.save(toSaveMovieCommand(movie));
    }
  }
  
  private SaveMovieCommand toSaveMovieCommand(Movie movie) {
    return SaveMovieCommand.builder()
        .sourceId(movie.sourceId())
        .title(movie.title())
        .releaseDate(movie.releaseDate())
        .originalLanguage(movie.originalLanguage())
        .build();    
  }
}
