package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.Objects;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.SurrogateId;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.domain.command.RehydrateMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

import lombok.Builder;

@Builder
public record RehydrateMovie(
    PublicId id, 
    SurrogateId surrogateId, 
    TmdbId tmdbId,
    Version version,
    Title title,
    ReleaseDate releaseDate,
    Score score,
    LanguageCode originalLanguage,
    Overview overview) implements RehydrateMovieCommand {

  public RehydrateMovie {
    Objects.requireNonNull(id, "movie id is required");
    Objects.requireNonNull(surrogateId, "surrogate id is required");
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(title, "title is required");
  }
}
