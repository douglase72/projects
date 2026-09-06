package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.domain.command.UpdateMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

import lombok.Builder;

@Builder
public record UpdateMovie(
    PublicId publicId,
    Version version,
    Title title,
    ReleaseDate releaseDate,
    Score score,
    LanguageCode originalLanguage,
    Overview overview) implements UpdateMovieCommand { 
  
  public UpdateMovie {
    Objects.requireNonNull(publicId, "publicId is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(title, "title is required");
  }
}
