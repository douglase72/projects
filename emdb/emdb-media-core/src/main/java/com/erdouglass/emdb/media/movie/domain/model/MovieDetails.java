package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;

import lombok.Builder;

@Builder
public record MovieDetails(
    Title title,
    ReleaseDate releaseDate,
    Score score,
    LanguageCode originalLanguage,
    Overview overview) {
  
  public MovieDetails {
    Objects.requireNonNull(title, "title is required");
  }
}
