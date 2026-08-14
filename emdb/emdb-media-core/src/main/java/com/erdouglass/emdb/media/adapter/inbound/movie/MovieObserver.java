package com.erdouglass.emdb.media.adapter.inbound.movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.MovieExtractedEvent;
import com.erdouglass.emdb.media.application.port.inbound.movie.SaveMovieCommand;
import com.erdouglass.emdb.media.application.port.inbound.movie.SaveMovieUseCase;
import com.erdouglass.emdb.media.domain.movie.MovieDetails;
import com.erdouglass.emdb.media.domain.movie.ReleaseDate;
import com.erdouglass.emdb.media.domain.movie.Title;
import com.erdouglass.emdb.media.domain.shared.LanguageCode;
import com.erdouglass.emdb.media.domain.shared.Overview;
import com.erdouglass.emdb.media.domain.shared.Score;

@ApplicationScoped
class MovieObserver {
  
  @Inject
  SaveMovieUseCase saveUseCase;
  
  public void onMovieExtractedEvent(@Observes MovieExtractedEvent event) {
    var details = MovieDetails.builder()
        .title(Title.of(event.title()))
        .releaseDate(event.releaseDate().map(ReleaseDate::of).orElse(null))
        .score(event.score().map(Score::of).orElse(null))
        .originalLanguage(event.originalLanguage().map(LanguageCode::of).orElse(null))
        .overview(event.overview().map(Overview::of).orElse(null))
        .build();
    var command = SaveMovieCommand.of(event.tmdbId(), details);
    saveUseCase.save(command);
  }
}
