package com.erdouglass.emdb.media.movie.adapter.in.messaging;

import com.erdouglass.emdb.ingest.messaging.MovieScraped;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;
import com.erdouglass.emdb.media.shared.domain.model.LanguageCode;
import com.erdouglass.emdb.media.shared.domain.model.Overview;
import com.erdouglass.emdb.media.shared.domain.model.Score;
import com.erdouglass.emdb.media.shared.domain.model.Title;

final class MovieMapper {
  
  private MovieMapper() { }
  
  public static SaveMovieCommand toMovieScrapedEvent(MovieScraped event) {
    var details = MovieDetails.builder()
        .title(Title.of(event.title()))
        .releaseDate(event.releaseDate() != null ? ReleaseDate.from(event.releaseDate()) : null)
        .score(event.score() != null ? Score.of(event.score()) : null)
        .originalLanguage(event.originalLanguage() != null ? LanguageCode.of(event.originalLanguage()) : null)
        .overview(event.overview() != null ? Overview.of(event.overview()) : null)        
        .build();
    return SaveMovieCommand.of(event.tmdbId(), details);
  }
}
