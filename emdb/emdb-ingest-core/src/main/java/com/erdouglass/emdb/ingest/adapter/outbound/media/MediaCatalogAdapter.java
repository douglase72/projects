package com.erdouglass.emdb.ingest.adapter.outbound.media;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.ingest.application.port.outbound.Media;
import com.erdouglass.emdb.ingest.application.port.outbound.MediaCatalog;
import com.erdouglass.emdb.ingest.application.port.outbound.MovieDto;
import com.erdouglass.emdb.media.movie.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.SaveMovieUseCase;

@ApplicationScoped
class MediaCatalogAdapter implements MediaCatalog {
  
  @Inject
  SaveMovieUseCase saveMovieUseCase;

  @Override
  public void load(Media media) {
    switch (media) {
      case MovieDto movie -> saveMovieUseCase.save(toSaveMovieCommand(movie));
    }
  }
  
  private SaveMovieCommand toSaveMovieCommand(MovieDto movie) {
    return SaveMovieCommand.builder()
        .tmdbId(movie.tmdbId())
        .title(movie.title())
        .releaseDate(movie.releaseDate().orElse(null))
        .score(movie.score().orElse(null))
        .originalLanguage(movie.originalLanguage().orElse(null))
        .build();    
  }
}
