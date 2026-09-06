package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.movie.domain.command.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

import lombok.Builder;

@Builder
public record SaveMovie(
    TmdbId tmdbId,
    Title title,
    ReleaseDate releaseDate,
    Score score,
    LanguageCode originalLanguage,
    Overview overview) implements SaveMovieCommand { 
  
  public SaveMovie {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(title, "title is required");
  }
}
