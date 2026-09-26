package com.erdouglass.emdb.media.movie.adapter.in.messaging;

import com.erdouglass.emdb.media.messaging.SaveMovieMessage;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;
import com.erdouglass.emdb.media.shared.domain.model.LanguageCode;
import com.erdouglass.emdb.media.shared.domain.model.Overview;
import com.erdouglass.emdb.media.shared.domain.model.Score;
import com.erdouglass.emdb.media.shared.domain.model.Title;
import com.erdouglass.emdb.media.shared.domain.model.TmdbId;

final class MovieMapper {
  
  private MovieMapper() { }
  
  public static SaveMovieCommand toSaveMovieCommand(SaveMovieMessage message) {
    var details = MovieDetails.builder()
        .title(Title.of(message.title()))
        .releaseDate(message.releaseDate() != null ? ReleaseDate.from(message.releaseDate()) : null)
        .score(message.score() != null ? Score.of(message.score()) : null)
        .originalLanguage(message.originalLanguage() != null ? LanguageCode.of(message.originalLanguage()) : null)
        .overview(message.overview() != null ? Overview.of(message.overview()) : null)        
        .build();
    return SaveMovieCommand.of(TmdbId.of(message.tmdbId()), details);
  }
}
