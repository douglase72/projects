package com.erdouglass.emdb.media.adapter.inbound.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.MovieExtractedEvent;
import com.erdouglass.emdb.media.application.port.inbound.movie.SaveMovieCommand;
import com.erdouglass.emdb.media.application.port.inbound.movie.SaveMovieUseCase;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.movie.Title;
import com.erdouglass.emdb.media.domain.shared.LanguageCode;
import com.erdouglass.emdb.media.domain.shared.Score;

@ApplicationScoped
class MovieObserver {
  
  @Inject
  SaveMovieUseCase saveUseCase;
  
  public void onMovieExtractedEvent(@Observes MovieExtractedEvent event) {
    var command = SaveMovieCommand.builder()
        .tmdbId(event.tmdbId())
        .title(Title.of(event.title()))
        .releaseDate(event.releaseDate().map(r -> ReleaseDate.of(r)).orElse(null))
        .score(event.score().map(s -> Score.of(s)).orElse(null))
        .originalLanguage(event.originalLanguage().map(l ->  LanguageCode.of(l)).orElse(null))
        .build(); 
    saveUseCase.save(command);
  }
}
